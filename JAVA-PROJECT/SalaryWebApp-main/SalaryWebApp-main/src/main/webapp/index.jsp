<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="https://lh3.googleusercontent.com/pw/AJFCJaX6FIwtFds-OdncRF5Eh4NpiaWx9y5nUmMn_KI2YHqRBY-rhoEmGjXsieFX5V3bhcPAEg-SIlGvKLeMjiSxyeCsQ2zhMfdYuaChvvgSOjIggEivvoXooPOzbyLRGe2WhfjeVswUVey-x_OVNAHnJDA=w190-h185-s-no?authuser=0">
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

    <title>Salary Prediction | Home Page</title>
  </head>
  <body class="bg-light">
    <header class="p-3 bg-dark text-white">
        <div class="container">
          <div class="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
            <a href="/" class="d-flex align-items-center mb-2 mb-lg-0 text-white text-decoration-none">
              <svg class="bi me-2" width="40" height="32" role="img" aria-label="Bootstrap"><use xlink:href="#bootstrap"></use></svg>
            </a>

            <ul class="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
              <li><a href="#" class="nav-link px-2 text-white">Home</a></li>
            </ul>

            <div class="text-end">
              <a href="loginPage" class="btn btn-outline-light me-2">Login</a>
              <a href="register" class="btn btn-warning">Register</a>
            </div>
          </div>
        </div>
      </header>
    <section>
        <div class="bg-image d-flex flex-column justify-content-center align-items-center" style="background-image: url('https://achkarlaw.com/wp-content/uploads/2022/08/Untitled-design-1-1120x445.jpg');  height: 50vh; background-position: center; background-repeat: no-repeat; background-size: cover; ">
            <h1 class="text-white">Salary Prediction</h1>
            <h5 class="text-white">Salary Prediction adalah sebuah aplikasi yang dapat memprediksi gaji dari seseorang berdasarkan kriteria tertentu.</h5>
        </div>
    </section>
    <section>
        <div class="container px-4 py-5" id="hanging-icons">
            <h2 class="pb-2 border-bottom text-center">Tujuan</h2>
            <h5 class="text-center">Aplikasi ini memiliki tujuan untuk membantu user untuk dapat memperkirakan gaji yang sesuai dengan kemampuan atau kriteria yang dipunyai user. Tidak hanya itu, aplikasi ini memungkinkan untuk membantu perekrutan oleh HRD dalam menentukan gaji yang akan diberikan kepada karyawannya.</h5>
        </div>
        <div class="container px-4 py-5" id="hanging-icons">
            <h2 class="pb-2 border-bottom text-center"></h2>
            <div class="row g-4 py-5 row-cols-1 row-cols-lg-3">
              <div class="col d-flex align-items-start">
                <div class="icon-square bg-light text-dark flex-shrink-0 me-3">
                  <svg class="bi" width="1em" height="1em"><use xlink:href="#toggles2"></use></svg>
                </div>
                <div>
                  <h2>Dataset</h2>
                  <p>Dataset yang digunakan pada aplikasi ini diambil dari situs Kaggle.com.</p>
                </div>
              </div>
              <div class="col d-flex align-items-start">
                <div class="icon-square bg-light text-dark flex-shrink-0 me-3">
                  <svg class="bi" width="1em" height="1em"><use xlink:href="#cpu-fill"></use></svg>
                </div>
                <div>
                  <h2>Model</h2>
                  <p>Dalam aplikasi ini untuk prediksi gaji menggunakan model machine learning yaitu Random Forest Regression.</p>
                </div>
              </div>
              <div class="col d-flex align-items-start">
                <div class="icon-square bg-light text-dark flex-shrink-0 me-3">
                  <svg class="bi" width="1em" height="1em"><use xlink:href="#tools"></use></svg>
                </div>
                <div>
                  <h2>Akurasi</h2>
                  <p>Akurasi yang didapatkan dalam machine learning mencapai 94%.</p>
                </div>
              </div>
            </div>
          </div>
    </section>
    <footer class="footer mt-auto py-3 bg-light">
        <div class="container text-center">
          <span class="text-muted ">@Copyright by Irfan Rasyid</span>
        </div>
      </footer>


    <!-- Optional JavaScript; choose one of the two! -->

    <!-- Option 1: Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>

    <!-- Option 2: Separate Popper and Bootstrap JS -->
    <!--
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
    -->
  </body>
</html>