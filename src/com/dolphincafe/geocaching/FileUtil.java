package com.dolphincafe.geocaching;

import java.awt.datatransfer.DataFlavor;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import javax.activation.MimetypesFileTypeMap;

/**
 *
 * @author Shigehiro Soejima
 */
class FileUtil {
    // JPG, GIF, TIFF are supported by geocaching.com
    public static final byte[] MAGIC_BMP = new byte[]{0x42, 0x4d};
    public static final byte[] MAGIC_GIF = new byte[]{0x47, 0x49, 0x46, 0x38};
    public static final byte[] MAGIC_JPG = new byte[]{(byte) 0xff, (byte) 0xd8};
    public static final byte[] MAGIC_PNG = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
    public static final byte[] MAGIC_TIFF_II = new byte[]{0x49, 0x49, 0x2a, 0x00};
    public static final byte[] MAGIC_TIFF_MM = new byte[]{0x4d, 0x4d, 0x00, 0x2a};

    public static final String MIME_TYPE_BMP = "image/bmp";
    public static final String MIME_TYPE_GIF = "image/gif";
    public static final String MIME_TYPE_JPG = "image/jpeg";
    public static final String MIME_TYPE_PJPG = "image/pjpeg";
    public static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_TIFF = "image/tiff";

    public static final DataFlavor uriListFlavor = createURIListFlavor();

    private static MimetypesFileTypeMap _map = getMimetypesFileMap();

    /**
     * Determines if the given file is an image file or not by its extension.
     *
     * @param file
     */
    public static boolean isImageFile(final File file) {
        if (file == null || file.length() == 0) {
            return false;
        }

        String mimeType = _map.getContentType(file);
        if (mimeType.startsWith("image")) {
            return magicCheck(file, mimeType);
        }

        return false;
    }

    /**
     * Determines if the given file is an image file of the specified mime type by
     * checking the magic number in the file header.
     *
     * @param file
     * @param mimeType
     */
    public static boolean magicCheck(final File file, String mimeType) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(file));
            if (mimeType.equals(MIME_TYPE_JPG) ||
                mimeType.equals(MIME_TYPE_PJPG)) {
                byte[] b = new byte[MAGIC_JPG.length];
                if (in.read(b) != MAGIC_JPG.length) {
                    return false;
                }
                return Arrays.equals(MAGIC_JPG, b);
            } else if (mimeType.equals(MIME_TYPE_GIF)) {
                byte[] b = new byte[MAGIC_GIF.length];
                if (in.read(b) != MAGIC_GIF.length) {
                    return false;
                }
                return Arrays.equals(MAGIC_GIF, b);
            } else if (mimeType.equals(MIME_TYPE_PNG)) {
                byte[] b = new byte[MAGIC_PNG.length];
                if (in.read(b) != MAGIC_PNG.length) {
                    return false;
                }
                return Arrays.equals(MAGIC_PNG, b);
            } else if (mimeType.equals(MIME_TYPE_TIFF)) {
                byte[] b = new byte[MAGIC_TIFF_II.length];
                if (in.read(b) != MAGIC_TIFF_II.length) {
                    return false;
                }
                return Arrays.equals(MAGIC_TIFF_II, b) ||
                    Arrays.equals(MAGIC_TIFF_MM, b);
            } else if (mimeType.equals(MIME_TYPE_BMP)) {
                byte[] b = new byte[MAGIC_BMP.length];
                if (in.read(b) != MAGIC_BMP.length) {
                    return false;
                }
                return Arrays.equals(MAGIC_BMP, b);
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    /**
     *
     */
    static MimetypesFileTypeMap getMimetypesFileMap() {
        MimetypesFileTypeMap map =
            (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
        map.addMimeTypes("image/png png");
        map.addMimeTypes("image/bmp bmp");

        return map;
    }

    /**
     *
     * @param uriList
     */
    static List<File> textURIListToFileList(String uriList) {
        List<File> list = new ArrayList<File>(1);
        StringTokenizer st = new StringTokenizer(uriList, "\r\n");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s.startsWith("#")) { // the line is a comment (as per the RFC 2483)
                continue;
            }
            try {
                URI uri = new URI(s);
                File file = new File(uri);
                if (file.length() != 0) {
                    list.add(file);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    //
    //
    //

    /**
     *
     */
    private static DataFlavor createURIListFlavor() {
        DataFlavor df = null;
        try {
            df = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (ClassNotFoundException e) {
            // shouldn't happen
        }

        return df;
    }
}
