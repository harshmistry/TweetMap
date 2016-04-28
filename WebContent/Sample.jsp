 <html>
    <head>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $('#call').click(function ()
            {
                $.ajax({
                    type: "post",
                    url: "WelcomeServlet", //this is my servlet
                   
                    }
                });
            });

        });
    </script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<body>
    input:<input id="txt1" type="text" name="" value="" /><br></br>
    output:<input id="op" type="text" name="" value="" /><br></br>
    <input type="button" value="Call Servlet" name="Call Servlet" id="call"/>
    <div id="output"></div>
</body>
</html>