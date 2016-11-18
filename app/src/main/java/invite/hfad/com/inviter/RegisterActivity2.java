package invite.hfad.com.inviter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RegisterActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        setTitle("Registration");
    }

    //Sign Up button
    public void onRegisterButton(View view){
        Intent intent = new Intent(RegisterActivity2.this, LoginActivity.class);
        Toast toast = Toast.makeText(this,"Success",Toast.LENGTH_SHORT);
        toast.show();
        startActivity(intent);
        finish();
    }
}
