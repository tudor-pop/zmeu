package io.zmeu.Resource;

public class ResourceFactory {
    /**
     * Create a copy of source but with new properties
     */
    public static Resource from(Resource source, Object properties) {
        if (properties == null) {
            return null;
        }
        var cloudState = new Resource();
        cloudState.setType(source.getType());
        cloudState.setResourceName(source.getResourceNameString());
        cloudState.setProperties(properties);
        cloudState.setId(source.getId());
        return cloudState;
    }
}
