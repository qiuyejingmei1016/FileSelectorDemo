FileSelectorDemo  	--->最早的文件选择版本；
FileSelectDemo.zip	--->比较新的文件选择版本(规范命名、新增文件选择历史记录)；
ImagePickerDemo   	--->仿微信图片视频文件选择demo，选择器类库形式添加；
imagepickerlibrary	--->图片选择及自定义相册(证件照、银行卡)拍照，选择器类库形式添加；
示例：
   ImagePicker.getInstance()
                .setTitle("选择图片")//设置标题
                .setMaxCount(1)//设置可选择数量
                .showCamera(true)//设置是否显示拍照按钮
                .setCameraTakeType(cameraTakeType)//设置拍照方式（自定义拍证件照还是原生拍照） 1身份证正面，2身份证反面，3银行卡，其他表示原生拍照
                .showImage(true)//设置是否展示图片
                .showVideo(false)//设置是否展示视频
                .start(this, REQUEST_SELECT_IMAGES_CODE);