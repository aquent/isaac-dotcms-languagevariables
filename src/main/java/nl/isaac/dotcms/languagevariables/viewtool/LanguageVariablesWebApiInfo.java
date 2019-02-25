package nl.isaac.dotcms.languagevariables.viewtool;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

public class LanguageVariablesWebApiInfo extends ServletToolInfo {

    @Override
    public String getKey () {
        return "languageVariables";
    }

    @Override
    public String getScope () {
        return ViewContext.REQUEST;
    }

    @Override
    public String getClassname () {
        return LanguageVariablesWebApiInfo.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        LanguageVariablesWebAPI viewTool = new LanguageVariablesWebAPI();
        viewTool.init( initData );

        setScope( ViewContext.REQUEST );

        return viewTool;
    }

}
