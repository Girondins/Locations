package girondins.locations;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Register extends Activity {
    private Button confirm;
    private EditText editName;
    private Controller cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        confirm = (Button)findViewById(R.id.confBtnID);
        editName = (EditText)findViewById(R.id.enterNameID);
        confirm.setOnClickListener(new ConfirmListener());
        cont = new Controller(this,savedInstanceState);
    }

    private class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            cont.start(editName.getText().toString());
        }
    }
}
