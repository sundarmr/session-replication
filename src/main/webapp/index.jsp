<%@page import="java.util.Date"%>
<%@page import="java.io.File,java.io.BufferedReader,java.io.FileReader" %>

<html>
<head>
<title>Testing OpenShift Session Replication</title>

<link rel="stylesheet" type="text/css" href="css/styles.css">

</head>
<body>

    <%
        // gear name
        // String gearId = System.getenv("OPENSHIFT_GEAR_UUID");
        File hostnameFile = new File("/etc/hostname");
        BufferedReader br = new BufferedReader(new FileReader(hostnameFile));
        String hostname = br.readLine();

    
        // get counter
        Integer counter = (Integer) session.getAttribute("demo.counter");
        if (counter == null) {
            counter = 0;
            session.setAttribute("demo.counter", counter);
        }

        // check for increment action
        String action = request.getParameter("action");

        if (action != null && action.equals("increment")) {
            // increment number
            counter = counter.intValue() + 1;

            // update session
            session.setAttribute("demo.counter", counter);
            session.setAttribute("demo.timestamp", new Date());
        }else if( action.equals("forward")){
            session.setAttribute("demo.counter", counter);
            session.setAttribute("demo.timestamp", new Date());
            response.sendRedirect("https://eap-app-eap-session.apps-crc.testing")
        }
    %>
    <h3>Testing OpenShift Session Replication</h3>
    <hr>

    <br> <b>Session Data</b>

    <br>
    <br>

    Session ID: <%=session.getId()%>

    <br>
    <br>

    <table>
        <tr>
            <th>Description</th>
            <th>Attribute Name</th>
            <th>Attribute Value</th>
        </tr>

        <tr>
            <td>Session counter</td>
            <td>demo.counter</td>
            <td><%= session.getAttribute("demo.counter") %></td>
        </tr>

        <tr>
            <td>Timestamp of last increment</td>
            <td>demo.timestamp</td>
            <td><%= session.getAttribute("demo.timestamp") %></td>
        </tr>
    </table>

    <br>
    <br> Page served by container: <%= hostname %> at <%= new java.util.Date() %>

    <br>
    <br>

    <a href="index.jsp?action=increment">Increment Counter</a> |
    <a href="index.jsp">Refresh</a>
    <a href="index.jsp?action=forward">click here for next pod</a>
</body>
</html>
