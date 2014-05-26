/*
 * Copyright 2013 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.simbasecurity.core.service.http;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simbasecurity.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

@org.springframework.stereotype.Controller
@RequestMapping("/simba-db")
@Transactional
public class DBViewer implements Controller {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("#{dbViewerProps['dbViewer.enabled']}")
    private String enabled;

    @Override
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {

    	if(enabled == null || ! Boolean.valueOf(enabled)) {
    		resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    		return null;
    	}

    	resp.setContentType("text/html");

        String table = req.getParameter("table");
        String query = req.getParameter("query");
        boolean asSQL = req.getParameter("asSQL") != null;
        PrintWriter out = resp.getWriter();
        printHeader(out);
        printForm(out, req.getRequestURI(), table, asSQL, query);

        if (!StringUtil.isEmpty(table)) {
            if (asSQL) {
                printRecordsAsSQL(out, table);
            } else {
                printRecords(out, "SELECT * FROM " + table);
            }
        } else if (!StringUtil.isEmpty(query)) {
            printRecords(out, query);
        }
        printFooter(out);
        out.close();
    	
        return null;
    }
    
    private void printHeader(PrintWriter out) {
        out.println("<html><head>");
        out.println("</head><body>");
    }

    private void printForm(PrintWriter out, String uri, String selectedTable, boolean asSQL, String query) {
        out.print("<form action='");
        out.print(uri);
        out.println("' method='post'>");

        out.println("<select name='table' onchange='this.form.submit()'>");
        out.println("<option value=''></option>");
        List<String> tables = getDBTables();
        for (String table : tables) {
            out.print("<option value='");
            out.print(table);
            out.print("'");
            if (table.equals(selectedTable)) {
                out.print(" selected");
            }
            out.print(">");
            out.print(table);
            out.println("</option>");
        }

        out.println("</select>");
        out.println("<input name='asSQL' type='checkbox' onchange='this.form.submit()' " + (asSQL ? " checked" : "")
                + ">As SQL?</input>");
        out.println("<br/>");
        out.println("<input type='submit' value='Refresh' />");
        out.println("</form>");

        out.println("<br/>");

        out.print("<form action='");
        out.print(uri);
        out.println("' method='post'>");
        out.print("<textarea name='query' type='textarea' cols='120' rows='5'>");
        if (query != null) {
            out.print(query);
        }
        out.println("</textarea>");
        out.println("<br/>");
        out.println("<input type='submit' value='Execute Query' />");
        out.println("</form>");
    }

    private void printRecords(PrintWriter out, String query) {
        if (query.toUpperCase().startsWith("SELECT ")) {
            printSelectStatement(out, query);
        } else {
            jdbcTemplate.execute(query);
            out.println("Query executed");
        }
    }

    private void printSelectStatement(PrintWriter out, String query) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);

        if (!result.isEmpty()) {
            out.println("<table border='1'>");
            // Column Names
            out.print("<tr>");
            for (String columnName : result.get(0).keySet()) {
                out.print("<th>");
                out.print(columnName);
                out.print("</th>");
            }
            out.println("</tr>");

            // Table Data
            for (Map<String, Object> map : result) {
                out.print("<tr>");
                for (Object columnValue : map.values()) {
                    out.print("<td>");
                    out.print(columnValue != null ? columnValue.toString() : "&nbsp;");
                    out.print("</td>");
                }
                out.println("</tr>");
            }
            out.println("</table>");
        }
    }

    private void printRecordsAsSQL(PrintWriter out, String table) {

        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM " + table);

        if (!result.isEmpty()) {
            out.println("<table border='1'>");
            // Table Data
            for (Map<String, Object> map : result) {
                out.print("INSERT INTO " + table + " VALUES (");
                boolean first = true;
                for (Object columnValue : map.values()) {
                    if (!first)
                        out.print(", ");
                    out.print(columnValue != null ? "'" + columnValue.toString() + "'" : "NULL");
                    first = false;
                }
                out.println(");<br/>");
            }
            out.println("</table>");
        }
    }

    private void printFooter(PrintWriter out) {
        out.println("</body></html>");
    }

    private List<String> getDBTables() {
        List<String> tables = new LinkedList<String>();

        List<String> resultList = jdbcTemplate
                .queryForList(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_SCHEM = 'PUBLIC' AND TABLE_TYPE = 'TABLE'",
                        String.class);

        for (String tableName : resultList) {
            tables.add(tableName);
        }
        return tables;
    }

}
