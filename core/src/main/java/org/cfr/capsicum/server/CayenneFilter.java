package org.cfr.capsicum.server;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cayenne.configuration.web.RequestHandler;
import org.cfr.capsicum.ICayenneRuntimeContext;
import org.cfr.commons.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;


public class CayenneFilter extends OncePerRequestFilter implements InitializingBean {

    protected ICayenneRuntimeContext cayenneRuntime;

    private PatternsRequestCondition conditionIncluded = null;

    private PatternsRequestCondition conditionExcluded = null;

    private String[] includeFilterPatterns;

    private String[] excludeFilterPatterns;

    private boolean alwaysUseFullPath = false;

    private boolean urlDecode = true;

    private boolean forceNewSession = true;

    /**
     * Creates WebInterceptor for default DataDomain.
     */
    public CayenneFilter(@Nonnull ICayenneRuntimeContext cayenneRuntime) {
        this.cayenneRuntime = Assert.notNull(cayenneRuntime, "Cayenne runtime is required");
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        if (includeFilterPatterns == null) {
            this.includeFilterPatterns = new String[] { "**/*" };
        }
        UrlPathHelper pathHelper = new UrlPathHelper();
        pathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
        pathHelper.setUrlDecode(urlDecode);
        conditionIncluded = new PatternsRequestCondition(includeFilterPatterns, pathHelper, null, true, true);
        if (excludeFilterPatterns != null) {
            conditionExcluded = new PatternsRequestCondition(excludeFilterPatterns, pathHelper, null, true, true);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (conditionExcluded != null) {
            PatternsRequestCondition excluded = conditionExcluded.getMatchingCondition(request);
            if (excluded != null) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        PatternsRequestCondition result = conditionIncluded.getMatchingCondition(request);
        if (result == null) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestHandler handler = cayenneRuntime.getInstance(RequestHandler.class);
        if (handler instanceof ISessionState) {
            ((ISessionState) handler).setForceNewSession(forceNewSession);
        }

        handler.requestStart(request, response);
        try {
            filterChain.doFilter(request, response);
        } finally {
            handler.requestEnd(request, response);
        }
    }

    /**
     * 
     * @return
     */
    public boolean isForceNewSession() {
        return forceNewSession;
    }

    /**
     * 
     * @param forceNewSession
     */
    public void setForceNewSession(boolean forceNewSession) {
        this.forceNewSession = forceNewSession;
    }

    /**
     * 
     * @return
     */
    public String[] getIncludeFilterPatterns() {
        return includeFilterPatterns;
    }

    /**
     * 
     * @param excludeFilterPatterns
     */
    public void setExcludeFilterPatterns(String... excludeFilterPatterns) {
        this.excludeFilterPatterns = excludeFilterPatterns;
    }

    /**
     * 
     * @return
     */
    public String[] getExcludeFilterPatterns() {
        return excludeFilterPatterns;
    }

    /**
     * 
     * @param includePatterns
     */
    public void setIncludeFilterPatterns(String... includePatterns) {
        this.includeFilterPatterns = includePatterns;
    }

    /**
     * Set if URL lookup should always use full path within current servlet
     * context. Else, the path within the current servlet mapping is used
     * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
     * Default is "false".
     */
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.alwaysUseFullPath = alwaysUseFullPath;
    }

    /**
     * Set if context path and request URI should be URL-decoded.
     * Both are returned <i>undecoded</i> by the Servlet API,
     * in contrast to the servlet path.
     * <p>Uses either the request encoding or the default encoding according
     * to the Servlet spec (ISO-8859-1).
     * <p>Default is "true", as of Spring 2.5.
     * @see #getServletPath
     * @see #getContextPath
     * @see #getRequestUri
     * @see WebUtils#DEFAULT_CHARACTER_ENCODING
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     * @see java.net.URLDecoder#decode(String, String)
     */
    public void setUrlDecode(boolean urlDecode) {
        this.urlDecode = urlDecode;
    }

}
