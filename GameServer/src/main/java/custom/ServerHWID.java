package custom;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class ServerHWID {
    private static final SystemInfo si = new SystemInfo();
    private static final String[] hwid =
            new String[]{
                    "QjRCOUQzOTBBNUIzNTc1QzEyNDEwRTUzN0VGRDU4Rjc=",
                    "REUzMDQxQTQ0NDQwQzIyRkYwNzEzODc2MkQ3OTUyRUE=",
                    "RjZFQ0JBQzgyRUZFRjhBNTQxMzJGMTJDNkY5RTE2MjI=",
                    "QUE0QTYyNDQwREE5MzRDNUQwRkNEQUNFODM3OTIyOTE=",
                    "RTlERUIwRUY0NzczREIzMDA1NDIxOTQ2Q0QzQjEzMTA=",
                    "Q0ZBMzNEQzkyNUI3RjU4NjNDMjBGOUFBQzhBNDFDNTQ=",
                    "QUI4MjNBN0IxQ0FFOEU2MEI5MDM4MjY0Q0Y0NTEzM0E=",
                    "RTE0RThBRUM1MzMyRkYxNDE5RjI2MkI4QzkwM0U3RTM=","RTFGMTFBOEQzRjUxN0JFMTAyQ0JCRTk3MzdDQTA1ODk=", "MTExRUZDQzFBNTQ5MkFDMzY3Qjg3MUQxQTZBODVBNTA=","MTE1REQ5OEZBNjY2RDM0MDVBMjA1REFEQkE2QzBDRUQ=", "MUZCMEE2NTVERkU3OTgzNzg4ODg4Njk4QUFCQjMzMUY=", "RUFDMkY3NjUxMTc3MEUxM0FCQkQ5NTE4OTI0MDJEMzg=", "RUEzMjQxMTgwRTU5N0Q3Njk2M0Y2NDNCMTBDMkU3QjA=", "MThBNTA3Q0JGREU3QjM5NjdFRDFCNUZFNUQ1RkM4RTc=", "OEIxOUMwRDQ5Rjk1RUI2MEZDMTY4RUE2OTRBQTkyMUY=", "NjVGNURBMzg5RUJBQzlFQUZCOTVERjQxMzEzMjQxNDE=", "MUQ4RDQ0RDQxNEY2N0ZEMDBDODEzMkUzOUU3NUZEMjI=", "QkI0Mzg5MTg5QjJBMzZGOTgwQUE5OUMwRTkwMzNGMkM=", "NENDQUJGQzJGNkNFMzY2REUyMzE4MEJCQTdFNzZCRUQ=", "MjdGODlBMkQ3NDc5QUI3N0Y5OUU5QTQzNDZFMjFFMTM=", "QzIwQkU4OEJBMDRBNDlGOTM3REUxMDY1MTY5MDQzNDM=", "M0JCMkE0M0U0QUEwOUM1REZCQzE5RTU0MzQxNjZCOTc=", "REVBMUM0Mzg0MEI0RTc5NjBGRjlEQzc0NDI2QjU2QjQ="};

    private static final HardwareAbstractionLayer hal = si.getHardware();

    public static String MD5(String text) throws NoSuchAlgorithmException {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = text.getBytes();
        final MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char[] str = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    public static JSONObject getComputerInfo() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("Processor", getProcessorInfo());
        jsonObject.put("Baseboard", geBaseboardInfo());
        jsonObject.put("HWDiskStore", getHWDiskStoreInfo());
        jsonObject.put("GlobalMemory", getGlobalMemoryInfo());
        return jsonObject;
    }

    private static JSONObject getProcessorInfo() {
        final JSONObject jsonObject = new JSONObject();
        final CentralProcessor.ProcessorIdentifier pi = hal.getProcessor().getProcessorIdentifier();
        jsonObject.put("Name", pi.getName());
        jsonObject.put("ProcessorID", pi.getProcessorID());
        return jsonObject;
    }

    /**
     * 獲取主機板相關訊息
     *
     * @return 主機板相關訊息
     */
    private static JSONObject geBaseboardInfo() {
        final JSONObject jsonObject = new JSONObject();
        final ComputerSystem cs = hal.getComputerSystem();
        /* 主機板 */
        jsonObject.put("Manufacturer", cs.getManufacturer()); // 製造商
        jsonObject.put("Model", cs.getModel()); // 型號
        jsonObject.put("HardwareUUID", cs.getHardwareUUID()); // 序號
        return jsonObject;
    }

    /**
     * 獲取磁盤相關訊息
     *
     * @return 磁盤相關訊息
     */
    private static JSONArray getHWDiskStoreInfo() {
        final JSONArray jsonArray = new JSONArray();
        final List<HWDiskStore> list = hal.getDiskStores();
        for (final HWDiskStore el : list) {
            final JSONObject obj = new JSONObject();
            obj.put("Serial", el.getSerial());
            obj.put("Size", el.getSize());
            jsonArray.add(obj);
        }
        return jsonArray;
    }

    /**
     * 獲取記憶體相關訊息
     *
     * @return 記憶體相關訊息
     */
    private static JSONObject getGlobalMemoryInfo() {
        final JSONObject jsonObject = new JSONObject();
        final GlobalMemory gm = hal.getMemory();
        jsonObject.put("Total", gm.getTotal());
        return jsonObject;
    }

    public static String getHWID() {
        try {
            return Base64.getEncoder().encodeToString(MD5(getComputerInfo().toString()).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e);
            return "";
        }
    }

    public static final boolean checkHwid(final String hwid) {
        for (String check : ServerHWID.hwid) {
            if (hwid.equals(check)) {
                return true;
            }
        }
        return false;
    }
}
