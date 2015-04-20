package seaclouds.utils.toscamodel.basictypes;

import seaclouds.utils.toscamodel.INamedEntity;
import seaclouds.utils.toscamodel.IType;

/**
 * Created by pq on 16/04/2015.
 */
public interface ITypeScalarUnit extends INamedEntity, IType {
    public IValueScalarUnit instantiate(float scalar,String unit);
}
