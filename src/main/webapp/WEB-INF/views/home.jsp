<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="<c:url value="/resources/favicon.ico"/>">
        <title>Statistic</title>
        <link href="<c:url value="/resources/css/bootstrap.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/resources/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
        <link href="<c:url value="/resources/css/ie10-viewport-bug-workaround.css"/>" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body style="padding-top: 80px;">
        <nav class="navbar navbar-inverse navbar-fixed-top">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar"> <span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span> </button> <a class="navbar-brand" href="#">Ukr 2 Pol</a> </div>
                <div id="navbar" class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="#">Statistic</a></li>
                        <li><a href="#Prediction">Prediction</a></li>
                        <li><a href="#Contact">Contact</a></li>
                    </ul>
                </div>
                <!--/.nav-collapse -->
            </div>
        </nav>
        
        <div class="container" style="width:950px; margin: auto;">
            <div>
                <h4>Choose parameters</h4>
                <form class="form-inline">
                    <div class="form-group">
                        <label for="passes">Pass:</label>
                        <select id="passes" class="form-control"></select>
                    </div>
                    <div class="form-group">
                        <label for="datetimepickerStart">Start date:</label>
                        <div class='input-group date' id='datetimepickerStart'>
                            <input type='text' class="form-control" />
                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-calendar"></span>
                            </span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="datetimepickerEnd">End date:</label>
                        <div class='input-group date' id='datetimepickerEnd'>
                            <input type='text' class="form-control" />
                            <span class="input-group-addon">
                                <span class="glyphicon glyphicon-calendar"></span>
                            </span>
                        </div>
                    </div>

                </form>
            </div>
            <canvas id="statisticChart" width="400" height="400"></canvas>
        </div>
        <!-- /.container -->

        <script>
            statUrl = "/prediction/api/stat";
            passesUrl = "/prediction/api/passes";
        </script>

        <script src="<c:url value="/resources/js/jquery.min.js"/>"></script>
        <script src="<c:url value="/resources/js/moment.js"/>"></script>
        <script src="<c:url value="/resources/js/bootstrap.min.js"/>"></script>
        <script src="<c:url value="/resources/js/bootstrap-datetimepicker.min.js"/>"></script>
        <script src="<c:url value="/resources/js/Chart.bundle.js"/>"></script>
        <script src="<c:url value="/resources/js/app.js"/>"></script>
        
    </body>

</html>