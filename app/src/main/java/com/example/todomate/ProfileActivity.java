package com.example.todomate;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.todomate.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("TodoMate - Profile");
        setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            binding.userEmailTextView.setText(user.getEmail());
        }

        binding.deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });

        binding.changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                // If account deletion fails, display a message to the user
                                Toast.makeText(ProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();

                                // if FirebaseAuthRecentLoginRequiredException is thrown, user needs to reauthenticate
                                // before deleting the account
                                // so sign out the user and redirect to the sign-in activity
                                if (task.getException() instanceof FirebaseAuthRecentLoginRequiredException) {
                                    mAuth.signOut();
                                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                    finish();
                                }

                                // You can also log the exception to help debug the issue
                                Log.e("ProfileActivity", "Failed to delete account", task.getException());
                            }
                        }
                    });
        }
    }

    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.show();

        // Initialize views in the dialog
        EditText currentPasswordEditText = dialog.findViewById(R.id.currentPasswordEditText);
        EditText newPasswordEditText = dialog.findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = dialog.findViewById(R.id.confirmPasswordEditText);

        // Set up the buttons
        Button changePasswordButton = dialog.findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                changePassword(dialog, currentPassword, newPassword, confirmPassword);
            }
        });
    }

    private void changePassword(Dialog dialog, String currentPassword, String newPassword, String confirmPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password length should be at least 6 characters
        if(newPassword.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password should contain at least one letter and one number
        if(!newPassword.matches(".*[a-zA-Z].*") || !newPassword.matches(".*[0-9].*")) {
            Toast.makeText(this, "Password should contain at least one letter and one number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password should not contain spaces
        if(newPassword.contains(" ")) {
            Toast.makeText(this, "Password should not contain spaces", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password should contain at least one uppercase letter
        if(newPassword.equals(newPassword.toLowerCase())) {
            Toast.makeText(this, "Password should contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            user.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPassword))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // User re-authenticated successfully, now change password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                } else {
                                                    // Password update failed, display a message to the user
                                                    Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // Re-authentication failed, display a message to the user
                                Toast.makeText(ProfileActivity.this, "Re-authentication failed. Please check your current password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
