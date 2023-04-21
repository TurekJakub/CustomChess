let cropper = NaN;
let init = false;

function changeDisplayedImage() {
  imageInput = $("#image-input");
  url = URL.createObjectURL(imageInput.prop("files")[0]);
  console.log(cropper);
  image = document.getElementById("cropper-image");

  if (!init) {
    document
      .getElementById("cropper-container")
      .style.setProperty("width", "calc(100vw * 3/16 -5)");
    document.getElementById("cropper-container").style = "height:20vh";
    init = true;
  }

  cropper =
    cropper ||
    new Cropper(image, {
      aspectRatio: 1 / 1,
      viewMode: 1,
      responsive: true,
      minContainerWidth: 0,
      minContainerHeight: 0,
      cropBoxResizable: false,
      minCropBoxHeight: 40,
      minCropBoxWidth: 40,
    });

  cropper.replace(url);
}
function resizeForm() {
  $("#form-container").removeClass("w-25");
  $("#form-container").removeClass("w-50");
  $("#form-container").removeClass("w-75");
  if ($(window).width() < 576) {
    $("#form-container").addClass("w-75");
  } else if ($(window).width() < 1400) $("#form-container").addClass("w-50");
  else $("#form-container").addClass("w-25");
  console.log($(window).width());
}
function makeSignUpRequest() {
  imageBlop = cropper.getCroppedCanvas().toBlob(
    function (blob) {
      return blob;
    },
    "image/jpeg",
    1
  );
  profilePicture = new File(
    [image],
    $("#username-input").val() + "_picture.jpge",
    { type: "image/jpeg" }
  );
  container = new DataTransfer();
  container.items.add(profilePicture);
  document.getElementById("image-input").files = container.files;
  document.getElementById("form").submit();
}
