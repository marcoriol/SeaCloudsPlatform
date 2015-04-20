package seaclouds.utils.toscamodel.impl;

import seaclouds.utils.toscamodel.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by pq on 20/04/2015.
 */
class SchemaValue implements  IValueStruct {
    @Override
    public Map<String, IValue> get() {
        return new Mappable();
    }

    class Mappable implements Map<String,IValue> {
        @Override
        public int size() {
            return properties.size();
        }

        @Override
        public boolean isEmpty() {
            return type.allProperties().isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return type.allProperties().containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            if (properties.containsValue(value))
                return true;
            else
            {
                for (IProperty p: type.allProperties().values())
                    if(p.defaultValue().equals(value))
                        return true;
            }
            return false;
        }

        @Override
        public IValue get(Object key) {
            IValue v = properties.get(key);
            if(v == null){
                IProperty p = type.allProperties().get(key);
                if(p != null)
                    v = p.defaultValue();
            }
            return v;
        }

        public boolean canPut(String key, IValue value) {
            IProperty p = type.allProperties().get(key);
            if(p == null)
                return false;
            if(p.type() instanceof ISchemaDefinition && value.type() instanceof ISchemaDefinition) {
                return ((ISchemaDefinition) p.type()).isCompatible((ISchemaDefinition)value.type());
            } else {
                return p.type().equals(value.type());
            }
        }
        @Override
        public IValue put(String key, IValue value) {
            if(canPut(key,value)) {
                IValue put = properties.put(key, value);
                if(put == null)
                    put = type.allProperties().get(key).defaultValue();
                return put;
            }
            else
                throw new UnsupportedOperationException();
        }

        @Override
        public IValue remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ? extends IValue> m) {
            m.forEach( (k,v)-> { if(!canPut(k,v)) throw new UnsupportedOperationException(); } );
            properties.putAll(m);
        }

        @Override
        public void clear() {
            properties.clear();
        }

        @Override
        public Set<String> keySet() {
            return type.allProperties().keySet();
        }

        @Override
        public Collection<IValue> values() {
            Collection<IValue> values = new ArrayList<>();
            values.addAll(properties.values());
            type.allProperties().values().forEach(v -> values.add(v.defaultValue()));
            return Collections.unmodifiableCollection(values);
        }

        @Override
        public Set<Entry<String, IValue>> entrySet() {
            // TODO
            throw new NotImplementedException();
        }

        @Override
        public boolean equals(Object o) {
            return properties.equals(o);
        }

        @Override
        public int hashCode() {
            return properties.hashCode();
        }
    }
    final Map<String,IValue> properties;
    final ISchemaDefinition type;
    public SchemaValue(ISchemaDefinition type, Map<String,IValue> properties){
        this.type = type;
        this.properties = properties;
    }

    public ISchemaDefinition type() {
        return type;
    }

}
