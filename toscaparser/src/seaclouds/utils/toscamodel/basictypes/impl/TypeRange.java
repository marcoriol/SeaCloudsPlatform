package seaclouds.utils.toscamodel.basictypes.impl;

import seaclouds.utils.toscamodel.IConstraint;
import seaclouds.utils.toscamodel.IType;
import seaclouds.utils.toscamodel.basictypes.ITypeRange;
import seaclouds.utils.toscamodel.basictypes.IValueRange;
import seaclouds.utils.toscamodel.impl.CoercedType;

import java.util.Collections;

/**
 * Created by johnnyfreak on 21/04/15.
 */
public class TypeRange implements ITypeRange {
    static ITypeRange instance = new TypeRange();
    public static ITypeRange instance() { return instance; }

    class RangeValue implements IValueRange{
        int lowerBound;
        int upperBound;

        public RangeValue(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public RangeValue(Integer lowerBound, Integer upperBound){
            this(lowerBound.intValue(), upperBound.intValue());
        }

        @Override
        public int lowerBound() {
            return this.lowerBound;
        }

        @Override
        public int upperBound() {
            return this.upperBound;
        }

        @Override
        public IType type() {
            return instance;
        }
    }

    @Override
    public IValueRange instantiate(Object value) {
        throw new IllegalArgumentException();
    }

    @Override
    public IType coerce(IConstraint constraint) {
        return new CoercedType(this, Collections.singleton(constraint));
    }

    @Override
    public boolean derivesFrom(IType parent) {
        return this == parent;
    }

    @Override
    public IValueRange instantiate(Object lowerBound, Object upperBound) {
        if( lowerBound instanceof Integer &&
            upperBound instanceof  Integer) return instantiate((Integer) lowerBound, (Integer) upperBound);
        throw new IllegalArgumentException();
    }

    @Override
    public IValueRange instantiate(int lowerBound, int upperBound) {
        return new RangeValue(lowerBound, upperBound);
    }

    @Override
    public IValueRange instantiate(Integer lowerBound, Integer upperBound) {
        return instantiate(lowerBound.intValue(), upperBound.intValue());
    }

    @Override
    public IValueRange instantiate(String lowerBound, String upperBound) {
        return instantiate(Integer.valueOf(lowerBound), Integer.valueOf(upperBound));
    }



}
