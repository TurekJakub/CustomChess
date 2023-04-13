let cropper = NaN;

function changeDisplayedImage() {
    imageInput = $("#image-input")
    url = URL.createObjectURL(imageInput.prop('files')[0])

    image = document.getElementById('cropper-image')

    cropper = cropper || new Cropper(image, {
        aspectRatio: 1 / 1,
        viewMode: 1,
    })

    cropper.replace(url)


}
