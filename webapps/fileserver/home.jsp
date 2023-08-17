<%@ page session="false" %>

<%
    boolean admin=request.isUserInRole("fadmin");
    boolean user=request.isUserInRole("fuser");
%>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Home</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.2.0/css/bootstrap.min.css"
            integrity="sha512-XWTTruHZEYJsxV3W/lSXG1n3Q39YIWOstqvmFsdNEEQfHoZ6vm6E9GK2OrF6DSJSpIbRbi+Nn0WDPID9O7xB2Q=="
            crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="./css/main.css" />
        <link rel="stylesheet" href="./css/home.css">
        <!-- <link rel="stylesheet" href="./css/welcome.css" /> -->
        <script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
    </head>

    <body>

        <div class="main-container">
           
            <div>
                <h4>Cloud Data Backup and Recovery</h4><br>
                <h1>
                    Welcome <%=request.getRemoteUser()%>
                </h1>
                <div>
                    <div>
                        <form action="FileUpload" method="post" enctype="multipart/form-data">
                            <input class="form-control form-control-lg" name="file" id="thefileinput" type="file" />
                            <button type="submit" class="btn btn-dark butt mt-3">Upload</button>
                        </form>
                        <button type="button" class="btn btn-dark butt mt-3" onclick="listFiles();">List Files</button>
                    </div>
                    <dir>
                        <div class="table-container">
                        </div>
                    </dir>
                </div>
            </div>
        </div>
        <div class="toast-container position-fixed bottom-0 end-0 p-3">
        </div>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.2.0/js/bootstrap.bundle.min.js"
        integrity="sha512-9GacT4119eY3AcosfWtHMsT5JyZudrexyEVzTBWV3viP/YfB9e2pEy3N7WXL3SV6ASXpTU0vzzSxsbfsuUH4sQ=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script>
            function runToast(){
                const toasts = document.getElementsByClassName('toast')
                for (i = 0; i < toasts.length; i++) {
                    let elem = toasts.item(i)
                    let elem_tost = new bootstrap.Toast(elem)
                    elem_tost.show()
                }
            }
            runToast();
        </script>
        <script>
            function listFiles(){
                $.ajax({
                    url:'listfiles',
                    type:'GET',
                    dataType: 'json'
                }).then(function(response1){
                        if(response1.err){
                            for(let i=0;i<response1.error.length;i++){
                                $('.toast-container')[0].innerHTML="";
                                $('.toast-container')[0].innerHTML+=`
                                <div class="toast align-items-center text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
                                    <div class="d-flex">
                                        <div class="toast-body">
                                            `+response.error[i]+`
                                        </div>
                                        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                                    </div>
                                </div>
                                `
                            }
                            runToast();
                        }else{
                            if(response1.files.length>0){
                                $('.table-container')[0].innerHTML="";
                                $('.table-container')[0].innerHTML+=`<table class="sql-table"><thead><tr><th scope="col">#</th><th scope="col">File Name</th><th scope="col">Download</th></tr></thead><tbody class="table-body-contents">`;
                                for (let i = 0; i < response1.files.length; i++) {
                                    $('.table-body-contents')[0].innerHTML+=`
                                        <tr>
                                        <td data-label="#">`+i+`</td>
                                        <td data-label="File Name">`+response1.files[i]+`</td>
                                        <td data-label="Download : ">`+`<form action="downloadFile" method="post">
                                                                        <input type="hidden" name="filename" value="`+response1.files[i]+`">
                                                                        <button type="submit" class="btn btn-dark butt mt-3"> Download </button>
                                                                        </form></td>
                                        </tr>
                                        `
                                }
                                $('.table-container')[0].innerHTML+=`</tbody></table>`;
                            }
                        }
                    });
            }
        </script>
    </body>

    </html>