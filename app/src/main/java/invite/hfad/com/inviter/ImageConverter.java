package invite.hfad.com.inviter;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Daryl on 9/1/2017.
 */

public class ImageConverter {

    public static byte[] compress_image(ContentResolver  contentResolver, Uri uri){
        File file = new File(uri.getPath());
        int imagesize = (int)file.length();
        InputStream imageStream = null;
        try {
            imageStream = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bmp = BitmapFactory.decodeStream(imageStream);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (imagesize > 30000) {
            int quality_scale = (30000 / imagesize) * 100;
            bmp.compress(Bitmap.CompressFormat.JPEG, quality_scale, stream);
        }
        else
        {
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        }


        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
