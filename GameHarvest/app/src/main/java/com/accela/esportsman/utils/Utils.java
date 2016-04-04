package com.accela.esportsman.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Patterns;
import android.widget.ImageButton;

import com.accela.framework.AMApplication;
import com.accela.framework.model.AddressModel;
import com.accela.framework.model.CompactAddressModel;
import com.accela.esportsman.data.AccountManager;
import com.accela.mobile.AMLogger;
import com.accela.mobile.AccelaMobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by eyang on 8/18/15.
 */
public class Utils {
    private static final String ALGO = "AES";

    private static final byte[] keyValue = new byte[]{'a', 'c', 'c', 'e', 'l', 'a', 'm', 'o', 'b', 'i', 'l', 'e', 'a', 'p', 'p', 's'};

    public static void saveData(final String filename, final String dataToSave) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                File dir = AMApplication.mContext.getFilesDir();
                File file = new File(dir, filename);
                file.delete();
                FileOutputStream outputStream = null;
                try {
                    outputStream = AMApplication.mContext.openFileOutput(filename, AMApplication.mContext.MODE_PRIVATE);
                    outputStream.write(encrypt(dataToSave));
                } catch (Exception e) {
                    AMLogger.logError(e.toString());
                } finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        AMLogger.logError(e.toString());
                    }
                }
            }
        });
    }

    public static String getLocalData(String filename) {
        String content = null;
        try {
            FileInputStream inputStream = AMApplication.mContext.openFileInput(filename);
            File dir = AMApplication.mContext.getFilesDir();
            File file = new File(dir, filename);
            byte[] fileContent = new byte[(int) file.length()];
            inputStream.read(fileContent);
            String str = new String(fileContent);
            str.toString();
            content = decrypt(fileContent);
        } catch (FileNotFoundException e) {
            AMLogger.logError(e.toString());
        } catch (IOException e) {
            AMLogger.logError(e.toString());
        } catch (Exception e) {
            AMLogger.logError(e.toString());
        }
        return content;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static String getDate(long millSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDate(Date date, String format) {
        if (date==null)
            return "";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public static String[] convertListToArray(List<String> llist, String[] array) {
        array = new String[llist.size()];
        llist.toArray(array);
        return array;
    }

    public static String[] convertListToArray(List<String> llist) {
        String[] array = new String[llist.size()];
        llist.toArray(array);
        return array;
    }


    public static String getAddressFullLine(CompactAddressModel address) {
        if (address == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (address.getAddressLine1() != null && address.getAddressLine1().length() > 0) {
            stringBuffer.append(address.getAddressLine1());
        }
        if (address.getAddressLine2() != null && address.getAddressLine2().length() > 0) {
            stringBuffer.append("\n");
            stringBuffer.append(address.getAddressLine2());
        }
        if (address.getCity() != null && address.getCity().length() > 0) {
            stringBuffer.append("\n");
            stringBuffer.append(address.getCity());
        }
        if (address.getState_text() != null && address.getState_text().length() > 0) {
            stringBuffer.append(", ");
            stringBuffer.append(address.getState_text());
        }
        if (address.getPostalCode() != null && address.getPostalCode().length() > 0) {
            stringBuffer.append(" ");
            stringBuffer.append(address.getPostalCode());
        }
        return stringBuffer.toString();
    }

    public static String getAddressFullLine(AddressModel address) {
        if (address == null) {
            return "";
        }

        StringBuffer stringBuffer = new StringBuffer();
        if (address.getAddressLine1() != null && address.getAddressLine1().length() > 0) {
            stringBuffer.append(address.getAddressLine1());
        }

        if (address.getAddressLine2() != null && address.getAddressLine2().length() > 0) {
            stringBuffer.append("\n");
            stringBuffer.append(address.getAddressLine2());
        }

        if (address.getCounty()!=null && address.getCounty().length()>0){
            stringBuffer.append(", ").append(address.getCounty());
        }

        if (address.getCity() != null && address.getCity().length() > 0) {
            stringBuffer.append("\n");
            stringBuffer.append(address.getCity());
        }

        if (address.getState_text() != null && address.getState_text().length() > 0) {
            stringBuffer.append(", ");
            stringBuffer.append(address.getState_text());
        }

        if (address.getPostalCode() != null && address.getPostalCode().length() > 0) {
            stringBuffer.append(" ");
            stringBuffer.append(address.getPostalCode());
        }

        if (address.getZip() != null && address.getZip().length() > 0) {
            stringBuffer.append(" ");
            stringBuffer.append(address.getZip());
        }

        return stringBuffer.toString();
    }


    public static byte[] encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        byte[] encryptedValue = Base64.encode(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    public static String decrypt(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;

    }

    private static Key generateKey() throws Exception {

        Key key = new SecretKeySpec(keyValue, ALGO);

        return key;
    }

    public static void getEnvironment(String environmentStr) {
        switch (environmentStr) {
            case "PROD":
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.PROD;
                break;
            case "DEV":
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.DEV;
                break;
            case "TEST":
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.TEST;
                break;
            case "STAGE":
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.STAGE;
                break;
            case "CONFIG":
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.CONFIG;
                break;
            case "SUPP":
                AccountManager.ENVIRONMENT = AccelaMobile.Environment.SUPP;
                break;
        }
    }

    public static void setImageButtonEnabled(Context ctxt, boolean status,
                                             ImageButton item, int iconResId) {

        Drawable icon;
        item.setEnabled(status);
        Drawable originalIcon = ContextCompat.getDrawable(ctxt, iconResId);
        if (status) {
            icon = convertDrawableToWhiteScale(originalIcon);
        } else {
            icon = originalIcon;
        }
        item.setImageDrawable(icon);
    }

    public static Drawable convertDrawableToWhiteScale(Drawable drawable) {
        Drawable res;
        if (drawable == null)
            return null;
        res = drawable.mutate();
        res.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        return res;
    }

    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (phoneNumber != null) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static String changeDateFormat(String date) {
        if (date != null) {
            String[] dateTokens = date.split("-");
            if(dateTokens!=null && dateTokens.length==3 && dateTokens[0].length() == 4) {
                StringBuilder builder = new StringBuilder();
                builder.append(dateTokens[1]);
                builder.append("/");
                builder.append(dateTokens[2]);
                builder.append("/");
                builder.append(dateTokens[0]);
                return builder.toString();
            } else {
                return date;
            }
        } else {
            return "";
        }
    }

    public static String[] reverseArray(String [] arr) {
        for(int i = 0; i < arr.length / 2; i++)
        {
            String temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
        return arr;
    }

    public static String formatDocNumber(String number) {
        StringBuilder builder = new StringBuilder();
        int start = 0;
        int end = 0;
        int count = 0;
        int numLength = number.length()/4;
        do {
            end += 4;
            count++;
            builder.append(number.substring(start, end));
            if (count != numLength) {
                builder.append("-");
            }
            start = end;

        } while (count < numLength);

        return builder.toString();
    }

    public static boolean isFirstRun(Context context) {
        final SharedPreferences reader = context.getSharedPreferences("LAUNCH_COUNT_PREF", Context.MODE_PRIVATE);
        final boolean first = reader.getBoolean("is_first", true);
        if(first){
            final SharedPreferences.Editor editor = reader.edit();
            editor.putBoolean("is_first", false);
            editor.commit();
        }
        return first;
    }
}
