package org.ubimix.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.json.utils.JavaObjectBuilder;
import org.ubimix.commons.parser.json.utils.JavaObjectVisitor;

/**
 * @author kotelnikov
 */
public class TreePresenter {

    @SuppressWarnings("unchecked")
    public static Map<Object, Object> copy(Map<Object, Object> map) {
        JavaObjectVisitor visitor = new JavaObjectVisitor();
        JavaObjectBuilder builder = new JavaObjectBuilder();
        visitor.visit(map, false, builder);
        return (Map<Object, Object>) builder.getTop();
    }

    public static String toString(Object value) {
        return value != null ? value.toString() : null;
    }

    private String fFieldName;

    public TreePresenter(String fieldName) {
        fFieldName = fieldName;
    }

    public boolean addChild(Map<Object, Object> map, int pos, Object innerObject) {
        boolean result = false;
        if (pos < 0) {
            return result;
        }
        Object value = map.get(fFieldName);
        if (value == null) {
            map.put(fFieldName, innerObject);
            result = true;
        } else {
            if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) value;
                if (pos >= 0 && pos <= list.size()) {
                    list.add(pos, innerObject);
                    result = true;
                }
            } else {
                if (pos == 0 || pos == 1) {
                    List<Object> list = new ArrayList<Object>();
                    map.put(fFieldName, list);
                    list.add(value);
                    list.add(pos, innerObject);
                    result = true;
                }
            }
        }
        return result;
    }

    public int getChildCount(Map<Object, Object> map) {
        Object value = map.get(fFieldName);
        int result = 0;
        if (value != null) {
            if (value instanceof List<?>) {
                result = ((List<?>) value).size();
            } else {
                result = 1;
            }
        }
        return result;
    }

    public Object getChildObject(Map<Object, Object> map, int pos) {
        Object value = map.get(fFieldName);
        Object result = null;
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = ((List<?>) value);
                result = pos >= 0 && pos < list.size() ? list.get(pos) : null;
            } else {
                result = pos == 0 ? value : null;
            }
        }
        return result;
    }

    public int getChildPosition(Map<Object, Object> map, Object nodeObject) {
        int result = -1;
        Object value = map.get(fFieldName);
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                result = indexOf(list, nodeObject);
            } else if (nodeObject.equals(value)) {
                result = 0;
            }
        }
        return result;
    }

    public <W> List<W> getChildren(
        Map<Object, Object> map,
        IValueFactory<W> factory) {
        List<W> result = new ArrayList<W>();
        Object value = map.get(fFieldName);
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                for (Object c : list) {
                    W child = factory.newValue(c);
                    if (child != null) {
                        result.add(child);
                    }
                }
            } else {
                W child = factory.newValue(value);
                if (child != null) {
                    result.add(child);
                }
            }
        }
        return result;
    }

    private int indexOf(List<?> list, Object nodeObject) {
        int idx = -1;
        if (!list.isEmpty()) {
            for (idx = 0; idx < list.size(); idx++) {
                if (same(nodeObject, list.get(idx))) {
                    break;
                }
            }
            if (idx >= list.size()) {
                idx = -1;
            }
        }
        return idx;
    }

    public boolean removeChild(Map<Object, Object> map, int pos) {
        boolean result = false;
        if (pos < 0) {
            return result;
        }
        Object value = map.get(fFieldName);
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                result = pos >= 0
                    && pos < list.size()
                    && list.remove(pos) != null;
                if (list.isEmpty()) {
                    map.remove(fFieldName);
                } else if (list.size() == 1) {
                    map.put(fFieldName, list.get(0));
                }
            } else if (pos == 0) {
                result = map.remove(fFieldName) != null;
            }
        }
        return result;
    }

    public boolean removeChild(Map<Object, Object> map, Object object) {
        Object value = map.get(fFieldName);
        boolean result = false;
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                list.remove(object);
                if (list.isEmpty()) {
                    map.remove(fFieldName);
                } else if (list.size() == 1) {
                    map.put(fFieldName, list.get(0));
                }
            } else if (value.equals(object)) {
                result = map.remove(fFieldName) != null;
            }
        }
        return result;
    }

    public void removeChildren(Map<Object, Object> map) {
        map.remove(fFieldName);
    }

    private boolean same(Object first, Object second) {
        return first == second;
    }

}