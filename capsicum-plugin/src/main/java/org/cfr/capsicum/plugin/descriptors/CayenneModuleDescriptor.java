package org.cfr.capsicum.plugin.descriptors;

import org.cfr.capsicum.plugin.ICayennePluginManager;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.descriptors.RequiresRestart;
import com.atlassian.plugin.module.ModuleFactory;

@RequiresRestart
public class CayenneModuleDescriptor extends AbstractModuleDescriptor<Object> {

    protected static final Logger log = LoggerFactory.getLogger(CayenneModuleDescriptor.class);

    private final ICayennePluginManager cayennePluginManager;

    private String domainFilename;

    @Autowired
    public CayenneModuleDescriptor(final ModuleFactory moduleFactory,
            final ICayennePluginManager cayennePluginManager) {
        super(moduleFactory);
        this.cayennePluginManager = cayennePluginManager;
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        final Element domainEl = element.element("domain");
        if (domainEl != null) {
            if (domainEl.attribute("file") != null) {
                domainFilename = domainEl.attributeValue("file");
            } else {
                domainFilename = domainEl.getTextTrim();
            }
        }
    }

    @Override
    public void enabled() {
        super.enabled();
        cayennePluginManager.addCayenneModule(this);
    }

    @Override
    public void disabled() {
        cayennePluginManager.removeCayenneModule(this);
        super.disabled();
    }

    @Override
    public Object getModule() {
        throw new UnsupportedOperationException("There is no module");
    }

    public String getDomainFilename() {
        return domainFilename;
    }

}