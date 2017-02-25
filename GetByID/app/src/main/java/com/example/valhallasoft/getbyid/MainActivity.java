package com.example.valhallasoft.getbyid;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private Button btn_todo;
    private Button btn_todos;
    private Button btn_usuarios;
    private TextView result;
    private Retrofit retrofit;
    private GetTodos getTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // The Permissions to ask user.
                String[] permissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION};
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions(MainActivity.this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }
        }
        retrofit = new Retrofit.Builder()
                .baseUrl("http://taxa.pe.hu/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getTodos = retrofit.create(GetTodos.class);
        btn_todos = (Button) findViewById(R.id.btn_todos);
        btn_todo = (Button) findViewById(R.id.btn_todo);
        btn_usuarios = (Button) findViewById(R.id.btn_usuario);

        btn_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTodos();
            }
        });
        btn_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTodo(2);
            }
        });
        btn_usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUsuario("Taxa01");
            }
        });
        result = (TextView) findViewById(R.id.result);
    }

    private void loadTodos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<Result> call = getTodos.all();
                try {
                    Response<Result> response = call.execute();
                    final Result result = response.body();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            displayResult(result);
                        }
                    });
                } catch (IOException e) {}
            }
        }).start();
    }

    private void displayResult(Result r) {
        if (r != null) {
            List<Todo> todos = r.getTodos();
            String tmp = "";

            for (Todo todo : todos) {
                tmp += todo.getId() + " | " + todo.getTitle() + " | " + (todo.getCompleted()? "Done" : "To Do") + "\n"+"\n";
            }
            print(tmp);
            result.setText(tmp);

        } else {
            result.setText("Error to get todos");
        }
    }

    private void loadTodo(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<Todo> call = getTodos.select(id);
                try {
                    Response<Todo> response = call.execute();
                    final Todo result = response.body();
                    System.out.println(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayTodo(result);
                        }
                    });
                } catch (IOException e) {}
            }
        }).start();
    }
    private void displayTodo(Todo todo) {
        if (todo != null) {
            String tmp = todo.getId() + " | " + todo.getTitle() + " | " + (todo.getCompleted()? "Done" : "To Do") + "\n";
            print(tmp);
            result.setText(tmp);
        } else {
            result.setText("Error to get todo");
        }
    }
    private void loadUsuario(final String usuario) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<Usuario> call = getTodos.getLogin(usuario);
                try {
                    Response<Usuario> response = call.execute();
                    final Usuario result = response.body();
                    System.out.println(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayUsuario(result);
                        }
                    });
                } catch (IOException e) {}
            }
        }).start();
    }
    private void displayUsuario(Usuario usuario) {
        if (usuario != null) {
            String tmp = usuario.getID_CHOFER() + " | " + " | " + (usuario.getCONTRASENIA()) + "\n";
            print(tmp);
            result.setText(tmp);
        } else {
            result.setText("Error to get todo");
        }
    }
    private void print(String print){
        System.out.println(print);
    }
}