package ru.regenix.jphp.runtime.reflection;

import ru.regenix.jphp.common.HintType;
import ru.regenix.jphp.runtime.env.Context;
import ru.regenix.jphp.runtime.env.Environment;
import ru.regenix.jphp.runtime.invoke.Invoker;
import ru.regenix.jphp.runtime.memory.ObjectMemory;
import ru.regenix.jphp.runtime.memory.support.Memory;
import ru.regenix.jphp.runtime.reflection.support.Entity;

public class ParameterEntity extends Entity {

    protected ClassEntity clazz;
    protected MethodEntity method;
    protected Memory defaultValue;

    protected boolean isReference;
    protected HintType type = HintType.ANY;
    protected String typeClass;
    protected String typeClassLower;

    public ParameterEntity(Context context) {
        super(context);
    }

    public Memory getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Memory defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ClassEntity getClazz() {
        return clazz;
    }

    private void setClazz(ClassEntity clazz) {
        this.clazz = clazz;
    }

    public MethodEntity getMethod() {
        return method;
    }

    public void setMethod(MethodEntity method) {
        this.method = method;
        this.clazz = method == null ? null : method.clazz;
    }

    public boolean isReference() {
        return isReference;
    }

    public void setReference(boolean reference) {
        isReference = reference;
    }

    public HintType getType() {
        return type;
    }

    public String getTypeClass() {
        return typeClass;
    }

    public void setType(HintType type) {
        this.type = type == null ? HintType.ANY : type;
    }

    public void setTypeClass(String typeClass) {
        this.typeClass = typeClass;
        this.typeClassLower = typeClass == null ? null : typeClass.toLowerCase();
    }

    public void setType(String type){
        this.type = HintType.of(type);
        if (this.type == null) {
            typeClass = type;
            typeClassLower = type.toLowerCase();
        } else {
            typeClass = null;
            typeClassLower = null;
        }
    }

    public boolean checkTypeHinting(Environment env, Memory value, boolean objectsCanNulls){
        if (type != HintType.ANY && type != null){
            switch (type){
                case SCALAR:
                    switch (value.getRealType()){
                        case BOOL:
                        case INT:
                        case DOUBLE:
                        case STRING:
                            return true;
                    }
                    return false;
                case NUMBER: return value.isNumber();
                case DOUBLE: return value.getRealType() == Memory.Type.DOUBLE;
                case INT: return value.getRealType() == Memory.Type.INT;
                case STRING: return value.isString();
                case BOOLEAN: return value.getRealType() == Memory.Type.BOOL;
                case ARRAY: return value.isArray();
                case CALLABLE:
                    return Invoker.valueOf(env, null, value) != null;
                default:
                    return true;
            }
        } else if (typeClass != null) {
            if (objectsCanNulls && value.isNull())
                return true;

            if (!value.isObject())
                return false;

            ObjectMemory object = value.toValue(ObjectMemory.class);
            ClassEntity oEntity = object.getReflection();

            return oEntity.isInstanceOf(typeClass);
        } else
            return true;
    }

    public boolean isArray(){
        return type == HintType.ARRAY;
    }

    public boolean isCallable(){
        return type == HintType.CALLABLE;
    }

    public boolean isOptional(){
        return defaultValue != null;
    }

    public boolean isDefaultValueAvailable(){
        return defaultValue != null;
    }

    public boolean canBePassedByValue(){
        return !isReference;
    }

    public boolean isPassedByReference(){
        return isReference;
    }

    public String getSignatureString(){
        return (type == HintType.ANY ? "" : type.toString() + " ")
                + (isReference ? "&" : "")
                    + ("$" + name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterEntity)) return false;
        if (!super.equals(o)) return false;

        ParameterEntity that = (ParameterEntity) o;

        if (isReference != that.isReference) return false;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
        result = 31 * result + (isReference ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
