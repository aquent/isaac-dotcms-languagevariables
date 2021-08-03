package nl.isaac.dotcms.languagevariables.util;

import java.util.ArrayList;
import java.util.List;

import com.dotcms.rendering.velocity.viewtools.content.util.ContentUtils;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PaginatedArrayList;
import com.dotmarketing.util.UtilMethods;

/**
 * Provides functionality to build a Lucene query.
 *
 * @author xander
 * @author jorith.vandenheuvel
 */
public class ContentletQuery {

    protected StringBuilder query = new StringBuilder();
    private final String structureName;

    // Paging & sorting
    private boolean usePaging = false;
    private int offset = 0;
    private int limit = -1;
    private String sortBy = "";
    private long totalResults = -1;

    public ContentletQuery(String structureName) {
        this(true, structureName);
    }

    public ContentletQuery(boolean include, String structureName) {
        this.structureName = structureName;
        query.append((include ? "+" : "-")).append("contentType:").append(structureName);
    }

    protected String getStructureName() {
        return structureName;
    }

    /**
     * @param include True if this must be included in the result, false otherwise
     * @param name    The field name
     * @param value   The value to search for
     */
    public void addFieldLimitation(boolean include, String name, String value) {
        query.append(" ").append((include ? "+" : "-")).append(structureName).append(".").append(name).append(":").append(escapeValue(value));
    }

    /**
     * @param include True if this must be included in the result, false otherwise
     * @param name    The field name
     * @param values  The values to search for
     */
    public void addFieldLimitations(boolean include, String name, String... values) {
        query.append(" ").append((include ? "+" : "-")).append("(");
        for (String value : values) {
            query.append(structureName).append(".").append(name).append(":").append(escapeValue(value)).append(" ");
        }
        query.append(")");
    }

    /**
     * @param name The field name
     * @param from The lower bound value
     * @param to   The upper bound value
     */
    public void addFieldRangeLimitation(String name, String from, String to) {
        query.append(" +").append(structureName).append(".").append(name).append(":[").append(from).append(" TO ").append(to).append("] ");
    }

    private String escapeValue(String value) {
        return value.replace(":", "\\:");
    }

    public void addLatestLiveAndNotDeleted(boolean live) {
        addLive(live);
        addWorking(true);
        addDeleted(false);
    }

    /**
     * Limit the results of the query to certain categories.
     *
     * @param include                  True if this must be included in the result, false otherwise
     * @param categoryVelocityVarNames The Velocity varnames of the categories to filter on
     */
    public void addCategoryLimitations(boolean include, String... categoryVelocityVarNames) {
        query.append(" ").append((include ? "+" : "-")).append("(");
        for (String categoryVelocityVarName : categoryVelocityVarNames) {
            query.append("categories:").append(escapeValue(categoryVelocityVarName)).append(" ");
        }
        query.append(")");
    }

    /**
     * Limit the results of the query to certain categories.
     *
     * @param include    True if this must be included in the result, false otherwise
     * @param categories The categories to filter on
     */
    public void addCategoryLimitations(boolean include, Category... categories) {
        String[] categoryVelocityVarNames = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            categoryVelocityVarNames[i] = categories[i].getCategoryVelocityVarName();
        }

        addCategoryLimitations(include, categoryVelocityVarNames);
    }

    /**
     * Adds +live to the query.
     *
     * @param live sets live to true or false
     */
    public void addLive(boolean live) {
        query.append(" +live:").append(live);
    }

    /**
     * Adds +working to the query.
     *
     * @param working sets working to true or false
     */
    public void addWorking(boolean working) {
        query.append(" +working:").append(working);
    }

    /**
     * Adds +deleted to the query.
     *
     * @param deleted sets deleted to true or false
     */
    public void addDeleted(boolean deleted) {
        query.append(" +deleted:").append(deleted);
    }

    /**
     * Adds a language limit to the query.
     *
     * @param language the language to set
     */
    public void addLanguage(Language language) {
        addLanguage(language.getId());
    }

    /**
     * Adds a language limit to the query.
     *
     * @param languageId the language id to add
     */
    public void addLanguage(Long languageId) {
        if (languageId != null) {
            addLanguage(languageId.toString());
        } else {
            Logger.warn(this, "Tried to add languageId Null!");
        }
    }

    /**
     * Adds a language limit to the query.
     *
     * @param languageId the language id to add
     */
    public void addLanguage(Integer languageId) {
        if (languageId != null) {
            addLanguage(languageId.toString());
        } else {
            Logger.warn(this, "Tried to add languageId Null!");
        }
    }

    /**
     * Adds a language limit to the query.
     *
     * @param languageId the language id to add
     */
    public void addLanguage(String languageId) {
        query.append(" +languageId:").append(languageId);
    }

    /**
     * Adds paging to the query.
     *
     * @param pageSize  The number of contentlets per page
     * @param pageIndex The page number to get results for
     */
    public void addPaging(Integer pageSize, Integer pageIndex) {
        ParamValidationUtil.validateParamNotNull(pageSize, "pageSize");
        ParamValidationUtil.validateParamNotNull(pageIndex, "pageIndex");

        this.offset = 1 + (pageIndex - 1) * pageSize;
        this.limit = pageSize;
        this.usePaging = true;
    }

    /**
     * Adds sorting to the query.
     *
     * @param fieldName the field to sort on. Must be in the format [structure name].[field name]
     */
    public void addSorting(String fieldName, boolean asc) {
        ParamValidationUtil.validateParamNotNull(fieldName, "sortBy");

        String sorting = structureName + "." + fieldName + " " + (asc ? "asc" : "desc");
        this.sortBy = UtilMethods.isSet(this.sortBy) ? this.sortBy + ", " + sorting : sorting;
    }

    /**
     * @return The total number of results, only when paging is set and the query has been executed. -1 otherwise.
     */
    public long getTotalResults() {
        return this.totalResults;
    }

    /**
     * Adds a host limit to the query.
     *
     * @param host the host bean to add to the query
     * @return The updated ContentletQuery
     */
    public ContentletQuery addHost(Host host) {
        return addHost(host.getIdentifier());
    }

    /**
     * Adds a host limit to the query.
     *
     * @param hostIdentifier the identifier of the host to add
     * @return The updated ContentletQuery
     */
    public ContentletQuery addHost(String hostIdentifier) {
        query.append(" +conhost:").append(hostIdentifier);
        return this;
    }

    /**
     * Adds a host limit to the query (given host AND System HOST.
     *
     * @param hostIdentifier the identifier of the host to add
     */
    public void addHostAndIncludeSystemHost(String hostIdentifier) {
        query.append(" +(conhost:SYSTEM_HOST conhost:").append(hostIdentifier).append(")");
    }

    /**
     * @return The resulting query
     */
    public String getQuery() {
        return query.toString();
    }

    /**
     * @return The resulting query as a StringBuilder object
     */
    public StringBuilder getQueryStringBuilder() {
        return query;
    }

    /**
     * Executes the query.
     *
     * @return The resulting List of Contentlets
     */
    public List<Contentlet> executeSafe() {
        try {
            if (this.usePaging) {
                PaginatedArrayList<Contentlet> contentlets = ContentUtils.pullPagenated(query.toString(), this.limit, this.offset,
                                                                                        this.sortBy,
                                                                                        APILocator.getUserAPI().getSystemUser(), null);
                this.totalResults = contentlets.getTotalResults();
                return contentlets;
            } else {
                return APILocator.getContentletAPI().search(query.toString(), this.limit, this.offset, this.sortBy,
                                                            APILocator.getUserAPI().getSystemUser(), false);
            }
        } catch (DotDataException e) {
            Logger.warn(this, "Data Exception while executing query", e);
        } catch (DotSecurityException e) {
            Logger.warn(this, "Security Exception while executing query", e);
        }

        return new ArrayList<>();
    }

    /**
     * Combines two ContentletQuery objects into one query.
     *
     * @param and             True if the resulting query must be an AND query. False if it must be an OR query.
     * @param contentletQuery The query to add
     */
    public void addQuery(boolean and, ContentletQuery contentletQuery) {
        if (and) {
            //just add them together
            query.append(" ").append(contentletQuery.getQuery());
        } else {
            //group them and make it an OR query
            query.insert(0, "(");
            query.append(") (");
            query.append(contentletQuery.getQuery());
            query.append(")");
        }
    }
}
