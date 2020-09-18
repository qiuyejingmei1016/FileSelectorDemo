package com.lcw.library.imagepicker.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @describe: 根据文件头信息获取文件类型格式
 * @author: yyh
 * @createTime: 2019/7/19 10:53
 * @className: FileTypeUtils
 */
public class FileTypeUtils {

    private FileTypeUtils() {
    }

    /**
     * 将文件头转换成16进制字符串
     *
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取文件头信息
     */
    private static String getFileHeader(InputStream is) {
        byte[] bytes = new byte[12];
        InputStream inputStream = null;
        try {
            is.read(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesToHexString(bytes);
    }

    /**
     * 判断文件类型
     */
    public static FileType getType(String filePath) throws IOException {
        FileInputStream inputStream = new FileInputStream(filePath);
        String fileHead = getFileHeader(inputStream);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        String fileHeadEnd = fileHead.substring(16);
        //视频处理avi类型
        if (fileHeadEnd.contains(FileType.AVI.getValue())) {
            return FileType.AVI;
        } else if (fileHeadEnd.contains(FileType.WEBP.getValue())) {//图片处理webp类型
            return FileType.WEBP;
        }
        String fileHeadStart = fileHead.substring(0, 8);
        FileType[] fileTypes = FileType.values();
        for (FileType type : fileTypes) {
            if (type.getValue().startsWith(fileHeadStart) || fileHeadStart.startsWith(type.getValue())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取文件类型
     * 1:图片文件类型  2:文档文件类型  3:视频文件类型  4:音频文件类型  5:种子文件类型  6:未知文件类型
     *
     * @param typeValue 文件类型
     */
    public static Integer isFileType(FileType typeValue) {
        //图片
        FileType[] pics = {FileType.JPEG, FileType.PNG, FileType.WEBP, FileType.GIF, FileType.TIFF,
                FileType.BMP, FileType.DWG, FileType.PSD};
        //视频
        FileType[] videos = {FileType.AVI, FileType.RAM, FileType.RM, FileType.MPG, FileType.MOV,
                FileType.MOV_x, FileType.ASF, FileType.MP4, FileType.FLV, FileType.MID};
        //音频
        FileType[] audios = {FileType.WAV, FileType.MP3, FileType.MP3_x};
        //文档
        FileType[] docs = {FileType.RTF, FileType.XML, FileType.HTML, FileType.CSS, FileType.JS,
                FileType.EML, FileType.DBX, FileType.PST, FileType.XLS_DOC, FileType.XLSX_DOCX,
                FileType.VSD, FileType.MDB, FileType.WPS, FileType.WPD, FileType.EPS, FileType.PDF,
                FileType.QDF, FileType.PWL, FileType.ZIP, FileType.RAR, FileType.JSP, FileType.JAVA,
                FileType.CLASS, FileType.JAR, FileType.MF, FileType.EXE, FileType.CHM};
        //种子
        FileType[] tottents = {FileType.TORRENT};

        //图片
        for (FileType fileType : pics) {
            if (fileType.equals(typeValue)) {
                return MediaFileType.PIC_TYPE;
            }
        }
        //文档
        for (FileType fileType : docs) {
            if (fileType.equals(typeValue)) {
                return MediaFileType.DOC_TYPE;
            }
        }
        //视频
        for (FileType fileType : videos) {
            if (fileType.equals(typeValue)) {
                return MediaFileType.VIDEO_TYPE;
            }
        }
        //音频
        for (FileType fileType : audios) {
            if (fileType.equals(typeValue)) {
                return MediaFileType.AUDIO_TYPE;
            }
        }
        //种子
        for (FileType fileType : tottents) {
            if (fileType.equals(typeValue)) {
                return MediaFileType.TOTTENT_TYPE;
            }
        }
        return MediaFileType.UNKNOWN_TYPE;
    }

    /**
     * 文件类型
     */
    public interface MediaFileType {
        int PIC_TYPE = 1; //图片文件类型
        int DOC_TYPE = 2; //文档文件类型
        int VIDEO_TYPE = 3; //视频文件类型
        int AUDIO_TYPE = 4; //音频文件类型
        int TOTTENT_TYPE = 5; //种子文件类型
        int UNKNOWN_TYPE = 6; //未知文件类型
    }

    /**
     * 文件类型头文件枚举类
     */
    public enum FileType {
        //图片
        JPEG("FFD8FF"),//JEPG
        PNG("89504E47"),//PNG
        WEBP("57454250"),//WEBP
        GIF("47494638"),//GIF
        TIFF("49492A00"),//TIFF
        BMP("424D"),//Windows Bitmap
        DWG("41433130"),//CAD
        PSD("38425053"),//Adobe Photoshop

        //视频
        AVI("41564920"),//AVI
        RAM("2E7261FD"),//Real Audio
        RM("2E524D46"),//Real Media
        MPG("000001BA"),//MPEG (mpg)
        MOV("6D6F6F76"),//Quicktime
        MOV_x("0000001466747970"),//mov可能有两种
        ASF("3026B2758E66CF11"),//Windows Media
        MP4("00000020667479706d70"),//MP4
        FLV("464C5601050000000900"),//FLV
        MID("4D546864"),//MIDI

        //音频
        WAV("57415645"),//Wave
        MP3("49443303000000002176"),//MP3
        MP3_x("FFF328C4000BE2B6"),//MP3

        //文档
        RTF("7B5C727466"),//Rich Text Format
        XML("3C3F786D6C"),//XML
        HTML("68746D6C3E"),//HTML
        CSS("48544D4C207B0D0A0942"),//CSS
        JS("696B2E71623D696B2E71"),//JS
        EML("44656C69766572792D646174653A"),//Email [thorough only]
        DBX("CFAD12FEC5FD746F"),//Outlook Express
        PST("2142444E"),//Outlook (pst)
        XLS_DOC("D0CF11E0"),
        XLSX_DOCX("504B030414000600080000002100"),//MS Word/Excel
        VSD("d0cf11e0a1b11ae10000"),//Visio
        MDB("5374616E64617264204A"),//MS Access
        WPS("d0cf11e0a1b11ae10000"),//WPS文字wps、表格et、演示dps都是一样的
        WPD("FF575043"),//WordPerfect
        EPS("252150532D41646F6265"),//Postscript
        PDF("255044462D312E"),//Adobe Acrobat
        QDF("AC9EBD8F"),//Quicken
        PWL("E3828596"),//Windows Password
        ZIP("504B0304"),//ZIP Archive
        RAR("52617221"),//RAR Archive
        JSP("3C2540207061676520"),//JSP Archive
        JAVA("7061636B61676520"),//JAVA Archive
        CLASS("CAFEBABE0000002E00"),//CLASS Archive
        JAR("504B03040A000000"),//JAR Archive
        MF("4D616E69666573742D56"),//MF Archive
        EXE("4D5A9000030000000400"),//EXE Archive
        CHM("49545346030000006000"),//CHM Archive

        //种子
        TORRENT("6431303A637265617465");//torrent

        private String value = "";

        private FileType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}