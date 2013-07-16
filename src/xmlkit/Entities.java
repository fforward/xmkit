package xmlkit;
import java.util.HashMap;
import java.util.Map;

public class Entities {
    
    private static final String[][] BASIC = {
            {"quot", "34"}, // " - double-quote
            {"amp", "38"}, // & - ampersand
            {"lt", "60"}, // < - less-than
            {"gt", "62"}, // > - greater-than   
            {"apos", "39"}, // XML apostrophe
    };
    
    public static final Entities XML;
    
    static {
        XML = new Entities();
        XML.addEntities(BASIC);
    }
    
    static interface EntityMap {
        void add(String name, int value);
        String getName(int value);
        int getValue(String name);
    }
    
    static class DefaultEntityMap implements EntityMap {
        
        private Map valueToName = new HashMap();
        private Map nameToValue = new HashMap();
        
        @Override
        public void add(String name, int value) {
            valueToName.put(new Integer(value), name);
            nameToValue.put(name, new Integer(value));
        }

        @Override
        public String getName(int value) {
            return (String)valueToName.get(value);
        }

        @Override
        public int getValue(String name) {
            Object value = nameToValue.get(name);
            if(value==null) {
                return -1;
            }
            
            return ((Integer)value).intValue();
        }
    }
    
    EntityMap map = new DefaultEntityMap();
    
    public void addEntities(String[][] entities) {
        for (int i = 0; i < entities.length; ++i) {
            addEntity(entities[i][0], Integer.parseInt(entities[i][1]));
        }
    }
    
    public void addEntity(String name, int value) {
        map.add(name, value);
    }
    
    public String escape(String str) {
        StringBuffer buf = new StringBuffer();
        
        for(int i=0; i < str.length(); i++) {
            char ch = str.charAt(i);
            String entity = getEntityName(ch);
            if(entity==null) {
              buf.append(ch);
              /*if(ch > 0x7F) {
                    int intValue = ch;
                    buf.append("&#");
                    buf.append(intValue);
                    buf.append(";");
                } else {
                    buf.append(ch);
                }*/
            } else {
                buf.append('&');
                buf.append(entity);
                buf.append(';');
            }
        }
        
        return buf.toString();
    }
    
    public String unescape(String str) {
        StringBuffer buf = new StringBuffer();
        
        for(int i=0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if(ch == '&') {
                int idx = str.indexOf(';', i+1);
                if(idx == -1) {
                    buf.append(ch);
                    continue;
                }
                
                String entity = str.substring(i+1, idx);
                int value = -1;
                if(entity.length()>1 && entity.charAt(0) == '#') {
                    char chAt1 = entity.charAt(1);
                    if(chAt1 == 'x' || chAt1 == 'X') {
                        value = Integer.valueOf(entity.substring(2), 16).intValue();
                    } else {
                        value = Integer.parseInt(entity.substring(1));
                    }
                } else {
                    value = getEntityValue(entity);
                }
                
                if(value==-1) {
                    buf.append('&');
                    buf.append(entity);
                    buf.append(';');
                } else {
                    buf.append((char)value);
                }
                
                i = idx;
            } else {
                buf.append(ch);
            }
        }
        
        return buf.toString();
    }
    
    public String getEntityName(int value) {
        return map.getName(value);
    }
    
    public int getEntityValue(String name) {
        return map.getValue(name);
    }
}
