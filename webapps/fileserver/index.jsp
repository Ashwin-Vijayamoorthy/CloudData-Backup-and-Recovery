<%@ page session="false" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.2.0/css/bootstrap.min.css"
            integrity="sha512-XWTTruHZEYJsxV3W/lSXG1n3Q39YIWOstqvmFsdNEEQfHoZ6vm6E9GK2OrF6DSJSpIbRbi+Nn0WDPID9O7xB2Q=="
            crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="./css/main.css">
        <link rel="stylesheet" href="./css/login.css">
        <script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
    </head>

    <body>

        <body class="bg-dark">
            <div class="main-container">
                <div class="login-container p-5">
                    <h4>Cloud Data Backup and Recovery</h4><br>
                    <a href="./home.jsp"><button type="button" class="btn btn-dark butt mt-3">User</button></a>
                    <a href="./admin.jsp"><button type="button" class="btn btn-dark butt mt-3">Admin</button></a>
                    <a href="./signup.jsp" class="link-text mt-2">new user?</a>
                </div>
            </div>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.2.0/js/bootstrap.bundle.min.js"
                integrity="sha512-9GacT4119eY3AcosfWtHMsT5JyZudrexyEVzTBWV3viP/YfB9e2pEy3N7WXL3SV6ASXpTU0vzzSxsbfsuUH4sQ=="
                crossorigin="anonymous" referrerpolicy="no-referrer"></script>
            <script>
                function runtoast() {
                    const toasts = document.getElementsByClassName('toast')
                    for (i = 0; i < toasts.length; i++) {
                        let elem = toasts.item(i)
                        let elem_tost = new bootstrap.Toast(elem)
                        elem_tost.show()
                    }
                }
                runtoast();
            </script>
        </body>

    </html>
    </body>

    </html>