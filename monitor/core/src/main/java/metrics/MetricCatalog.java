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
package metrics;

import com.google.common.collect.Maps;

import java.util.Map;


public class MetricCatalog {


    // Predefined Metrics in SeaClouds platform
    //TODO: Add remaining metrics (Annex C in D4.1 - Definition of the multi-deployment and monitoring strategies)
    public static enum PredefinedMetrics {
        JAVA_HEAP_USED (new Metric<>("java.heap.used", "Current heap size (bytes)", Long.class)),
        JAVA_HEAP_INIT (new Metric<>("java.heap.init", "Initial heap size (bytes)", Long.class)),
        JAVA_HEAP_COMMITED (new Metric<>("java.heap.commited", "Commited heap size (bytes)", Long.class)),
        JAVA_HEAP_MAX (new Metric<>("java.heap.max", "Max heap size (bytes)", Long.class) ),
        JAVA_NONHEAP_USED ( new Metric<>("java.nonheap.used", "Current non-heap size (bytes)", Long.class)),
        JAVA_THREADS_CURRENT ( new Metric<>("java.threads.current", "Current number of threads", Integer.class)),
        JAVA_THREADS_MAX (new Metric<>("java.threads.max", "Peak number of threads", Integer.class)),
        JAVA_TIME_START (new Metric<>("java.time.start", "Start time of Java process (UTC)", Long.class)),
        JAVA_TIME_UPTIME (new Metric<>("java.time.uptime", "Uptime of Java process (millis, elapsed since start)", Long.class)),
        JAVA_TIME_PROCESSCPU_TOTAL (new Metric<>("java.time.processcpu.total", "Process CPU time (total millis since start)", Double.class)),
        JAVA_TIME_PROCESSCPU_FRACTION_LAST (new Metric<>("java.time.processcpu.fraction.last", "Fraction of CPU time used, reported by JVM (percentage, last datapoint)", Double.class)),
        JAVA_TIME_PROCESSCPU_FRACTION_WINDOWED (new Metric<>("java.time.processcpu.fraction.windowed", "Fraction of CPU time used, reported by JVM (percentage, over time window)", Double.class)),
        JAVA_PROCESSORS_AVAILABLE (new Metric<>("java.processors.available", "Number of processors available to the Java virtual machine", Integer.class)),
        JAVA_SYSTEMLOAD_AVERAGE (new Metric<>("java.systemload.average", "System load average for the last minute", Double.class)),
        JAVA_PHYSICAL_MEMORY_TOTAL (new Metric<>("java.physical.memory.total", "The physical memory available to the operating system", Long.class)),
        JAVA_PHYSICAL_MEMORY_FREE (new Metric<>("java.physical.memory.free", "The free memory available to the operating system", Long.class)),

        WEBAPP_REQUESTS_TOTAL (new Metric<>("webapp.reqs.total", "Request count", Integer.class)),
        WEBAPP_REQUESTS_ERRORS (new Metric<>("webapp.reqs.errors", "Request errors", Integer.class)),
        WEBAPP_PROCESSING_TIME_TOTAL (new Metric<>("webapp.reqs.processingTime.total", "Total processing time, reported by webserver (millis)", Integer.class)),
        WEBAPP_PROCESING_TIME_MAX (new Metric<>("webapp.reqs.processingTime.max", "Max processing time for any single request, reported by webserver (millis)", Integer.class)),
        WEBAPP_PROCESSING_TIME_FRACTION_LAST (new Metric<>("webapp.reqs.processingTime.fraction.last", "Fraction of time spent processing, reported by webserver (percentage, last datapoint)", Double.class)),
        WEBAPP_PROCESSING_TIME_FRACTION_WINDOWED (new Metric<>("webapp.reqs.processingTime.fraction.windowed", "Fraction of time spent processing, reported by webserver (percentage, over time window)", Double.class)),

        ;

        private final Metric metric;

        PredefinedMetrics(Metric metric){
            this.metric = metric;
        }

        public Metric getValue(){
            return metric;
        }

        public static PredefinedMetrics getEnum(String name){
            PredefinedMetrics result = null;

            // Checking if the predefined metric catalog contains this id
            for(PredefinedMetrics enumMetric : PredefinedMetrics.values()){
                if(enumMetric.getValue().getId().equals(name)){
                    result = enumMetric;
                    break;
                }
            }

            return result;
        }
    }


    // Custom user-defined metric prefix
    public  final static String RUNTIME_METRIC_PREFIX = "custom.";

    private static MetricCatalog instance;
    private Map<String, Metric> metricMapping;

    private MetricCatalog(){
        metricMapping = Maps.newHashMap();

    }

    public static MetricCatalog getInstance(){
        if(instance == null)
            instance = new MetricCatalog();

        return instance;
    }

    public <T> Metric add(String id, String description, Class<T> type){
        Metric<T> newMetric = new Metric<>(id,description,type);

        if(!metricMapping.containsKey(id))
            metricMapping.put(id, newMetric);

        return newMetric;
    }

    public Metric getMetric(String metricID) {
        Metric result = null;

        // Checking if the predefined metric catalog contains this id
        for(PredefinedMetrics enumMetric : PredefinedMetrics.values()){
            if(enumMetric.getValue().getId().equals(metricID)){
                result = enumMetric.getValue();
                break;
            }
        }

        // The metric is not part of the predefined metrics
        if (result == null){
            result = metricMapping.get(metricID);
        }
        return result;
    }

    // TODO: Classify metrics by type (java, db...). Maybe with a filter by id


}
