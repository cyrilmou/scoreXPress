package fr.cm.scorexpress.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import static java.util.Collections.unmodifiableList;

public class ObjConfiguration implements Serializable {
    private static final long serialVersionUID = 7787829394427970479L;
    /**
     * Liste des configurations de tableau
     */
    private final ArrayList<ObjConfig> configs = new ArrayList<ObjConfig>();

    public boolean addConfig(final ObjConfig c) {
        return !c.isTemplate() && configs.add(c);
    }

    public boolean removeConfig(final ObjConfig c) {
        return configs.remove(c);
    }

    public Iterable<ObjConfig> getConfigs() {
        return unmodifiableList(configs);
    }

    public ObjConfig getConfig(final ConfigType configType) {
        try {
            final int index = configs.indexOf(new ObjConfig(configType, ""));
            if (index > -1) {
                final ObjConfig res = configs.get(index);
                if (!res.isTemplate()) {
                    return res;
                } else {
                    return ObjConfig.create(configType);
                }
            }
        } catch (Exception ignored) {
        }
        final ObjConfig config = ObjConfig.create(configType);
        if (config != null && !config.isTemplate()) {
            addConfig(config);
        }
        return config;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }
}
