package seniorproject.attendancetrackingsystem.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import seniorproject.attendancetrackingsystem.R;
import seniorproject.attendancetrackingsystem.fragments.ReportFragment;
import seniorproject.attendancetrackingsystem.fragments.WelcomeFragment;
import seniorproject.attendancetrackingsystem.helpers.DatabaseManager;
import seniorproject.attendancetrackingsystem.helpers.ServiceManager;
import seniorproject.attendancetrackingsystem.helpers.SessionManager;

public class StudentActivity extends AppCompatActivity {

  private BottomNavigationView mainNav;

  private WelcomeFragment welcomeFragment;
  private ReportFragment reportFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SessionManager session = new SessionManager(getApplicationContext());
    session.checkLogin();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_student);
    Toolbar toolbar = findViewById(R.id.toolbar);
    startService(new Intent(this, ServiceManager.class));
    setSupportActionBar(toolbar);
    mainNav = findViewById(R.id.main_nav);
    welcomeFragment = new WelcomeFragment();
    reportFragment = new ReportFragment();

    setFragment(welcomeFragment);
    Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
    getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
    getSupportActionBar().setSubtitle("/Home");
    mainNav.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
              case R.id.nav_home:
                setFragment(welcomeFragment);
                Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
                getSupportActionBar().setSubtitle("/Home");
                break;

              case R.id.nav_report:
                setFragment(reportFragment);
                Objects.requireNonNull(getSupportActionBar()).setLogo(R.drawable.kdefault);
                getSupportActionBar().setTitle("Ç.Ü. Attendance Tracking System");
                getSupportActionBar().setSubtitle("/Report");
                break;

              case R.id.logout:
                SessionManager session = new SessionManager(getApplicationContext());
                session.logoutUser();
                break;
              default:
                break;
            }
            return true;
          }
        });
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.main_frame, fragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.toolbar_menu_student, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.toString().equals("Change Password")) {

      buildAlertDialog().show();
    }

    return super.onOptionsItemSelected(item);
  }

  private AlertDialog.Builder buildAlertDialog(){
    final AlertDialog.Builder alert = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
    final LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);

    final EditText oldPassword = new EditText(this);
    oldPassword.setHint("Old password");
    oldPassword.setTextColor(Color.BLACK);
    oldPassword.setHintTextColor(Color.BLACK);
    oldPassword.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    oldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    oldPassword.setId(R.id.old_password);

    layout.addView(oldPassword);

    final EditText newPassword = new EditText(this);
    newPassword.setHint("New password");
    newPassword.setTextColor(Color.BLACK);
    newPassword.setHintTextColor(Color.BLACK);
    newPassword.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    newPassword.setId(R.id.new_password);

    layout.addView(newPassword);

    final EditText newPasswordRepeat = new EditText(this);
    newPasswordRepeat.setHint("Re-enter new password");
    newPasswordRepeat.setTextColor(Color.BLACK);
    newPasswordRepeat.setHintTextColor(Color.BLACK);
    newPasswordRepeat.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    newPasswordRepeat.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    newPasswordRepeat.setId(R.id.new_password_repeat);

    layout.addView(newPasswordRepeat);
    alert.setView(layout);


    alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String old_password = oldPassword.getText().toString();
        String new_password = newPassword.getText().toString();
        String new_password_repeat = newPasswordRepeat.getText().toString();

        if (old_password.isEmpty() || new_password.isEmpty() || new_password_repeat.isEmpty()) {
          Toast.makeText(getApplicationContext(), "Empty field error", Toast.LENGTH_SHORT).show();
          return;
        }

        if(!new_password.equals(new_password_repeat)){
          Toast.makeText(getApplicationContext(), "New passwords don't match", Toast
                  .LENGTH_SHORT).show();
          return;
        }
        Map<String, String> params = new HashMap<>();
        SessionManager session = new SessionManager(getApplicationContext());
        Map<String, String> userInfo = session.getUserDetails();
        params.put("old_password", old_password);
        params.put("new_password", new_password);
        params.put("user_type", userInfo.get(SessionManager.KEY_USER_TYPE));
        params.put("user_id", userInfo.get(SessionManager.KEY_USER_ID));
        DatabaseManager.getmInstance(getApplicationContext()).execute("change-password",params);
      }
    });


    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    return alert;
  }

  @Override
  public void onBackPressed() {
    setFragment(welcomeFragment);
    mainNav.setSelectedItemId(R.id.nav_home);
  }
}
