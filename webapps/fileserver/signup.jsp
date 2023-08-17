<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Signup</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.2.0/css/bootstrap.min.css"
    integrity="sha512-XWTTruHZEYJsxV3W/lSXG1n3Q39YIWOstqvmFsdNEEQfHoZ6vm6E9GK2OrF6DSJSpIbRbi+Nn0WDPID9O7xB2Q=="
    crossorigin="anonymous" referrerpolicy="no-referrer" />
  <link rel="stylesheet" href="./css/main.css">
  <link rel="stylesheet" href="./css/signup.css">
</head>

<body>

  <body class="bg-dark">
    <div class="main-container">
      
      <form action="signup" method="post">
        <div class="login-container p-5">
          <h4>Cloud Data Backup and Recovery</h4><br>
          <h1>Sign up</h1>
          <div class="mt-4">
            <div class="form-floating mb-3">
              <input type="text" name="username" class="form-control input-tf" id="floatingUsername"
                placeholder="User Name" autocomplete="off">
              <label for="floatingUsername">User Name</label>
            </div>
            <div class="form-floating">
              <input type="password" name="password" class="form-control input-tf" id="floatingPassword"
                placeholder="Password" autocomplete="off">
              <label for="floatingPassword">Password</label>
            </div>
          </div>
          <button type="submit" class="btn btn-dark mt-3 butt">Sign up</button>
          <a href="./home.jsp" class="link-text mt-2">registered user?</a>
        </div>
      </form>
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
</body>

</html>
