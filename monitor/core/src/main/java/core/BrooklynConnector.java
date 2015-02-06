/**
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package core;

import brooklyn.rest.client.BrooklynApi;
import brooklyn.rest.domain.SensorSummary;
import com.google.common.collect.Lists;
import metrics.BrooklynMetricLanguage;
import metrics.Metric;
import metrics.MetricCatalog;
import model.Module;
import model.exceptions.MonitorConnectorException;

import java.util.List;

public class BrooklynConnector implements Connector {
    private final static BrooklynMetricLanguage METRIC_TRANSLATOR = BrooklynMetricLanguage.getInstance();
    private final static MetricCatalog METRIC_CATALOG = MetricCatalog.getInstance();

    private Module module;
    private BrooklynApi endpoint;

    // Metrics available in every module
    private List<Metric> availableMetrics;

    private void  updateAvailableMetrics() throws MonitorConnectorException {

        List<SensorSummary> sensorSummaries = this.endpoint.getSensorApi().list(module.getParentApplication().getId(), module.getId());

        // It returns null if some error happened
        if (sensorSummaries == null)
            throw new MonitorConnectorException("Unable to fetch sensor catalog from Brooklyn for " + module);

        for(SensorSummary sensor : sensorSummaries){
            String seaCloudsMetricId  = METRIC_TRANSLATOR.getTranslation(sensor.getName());

            if(seaCloudsMetricId != null){
                // Translation known, adding it into the available metrics for this module

                /*
                 *   NOTE:
                 *   We assume that if a MetricLanguage has a translation, the Metric is also
                 *   included in the MetricCatalog, so it's not necessary to check if the MetricCatalog
                 *   returns null for the translated id.
                 */
                availableMetrics.add(METRIC_CATALOG.getMetric(seaCloudsMetricId));
            }else if (sensor.getName().startsWith(MetricCatalog.RUNTIME_METRIC_PREFIX)) {
                // Translation unknown, but it's a custom metric defined by the user in runtime
                try {
                    Metric<?> metric = METRIC_CATALOG.add(sensor.getName(), sensor.getDescription(), Class.forName(sensor.getType()));
                    METRIC_TRANSLATOR.addTranslation(sensor.getName(), sensor.getName());
                    this.availableMetrics.add(metric);
                } catch (ClassNotFoundException e) {
                    throw new MonitorConnectorException("Unable to parse custom sensor type for " + sensor.getName() + " with type " + sensor.getType());
                }
            } else{
                // Translation unknown and it is part of module setup (due Brooklyn ConfigKey), so ignoring it from now.
            }
        }
    }

    public BrooklynConnector(Module module, String endpoint) throws MonitorConnectorException {
        this.module = module;
        this.endpoint = new BrooklynApi(endpoint);
        this.availableMetrics = Lists.newArrayList();
        this.updateAvailableMetrics();
    }


    @Override
    public List<Metric> getAvailableMetrics() {
        return availableMetrics;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue(Metric<T> metric)  throws MonitorConnectorException{

        if(!availableMetrics.contains(metric))
            throw new MonitorConnectorException(metric.getId() + " metric doesn't exist in " + module.getId() + " module");

        T result = (T) endpoint.getSensorApi().get(module.getParentApplication().getId(), module.getId(), METRIC_TRANSLATOR.getInverseTranslation(metric.getId()), false);

        // It returns null if some error happened or sensors are not yet connected
        if (result == null)
            throw new MonitorConnectorException("Unable to fetch " + metric.getId() + " sensor from " + module.getId());

        return result;
    }


}
