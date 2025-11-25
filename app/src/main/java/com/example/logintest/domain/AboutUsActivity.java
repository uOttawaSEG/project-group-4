package com.example.logintest.domain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {
    
    private List<Course> courseList;
    private LinearLayout coursesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);
        
        //initializing instances for the scroll view inside the about_us.xml
        coursesContainer = findViewById(R.id.coursesContainer);
        
        //initializing the list of courses
        courseList=new ArrayList<>(Arrays.asList(
                new Course("GNG1105", "Engineering Mechanics"),
                new Course("GNG1106", "Fundamentals of Engineering Computation"),
                new Course("MCG1101", "Fundamentals of Mechanical Engineering "),
                new Course("CHG1371", "Numerical Methods and Engineering Computation in Chemical Engineering"),
                new Course("ITI1100", "Digital Systems I"),
                new Course("ITI1120", "Introduction to Computing I"),
                new Course("ITI1121", "Introduction to Computing II"),
                new Course("MAT1341", "Introduction to Linear Algebra"),
                new Course("MAT1348", "Discrete Mathematics for Computing"),
                new Course("MAT1320", "Calculus I"),
                new Course("MAT1322", "Calculus II"),
                new Course("CVG1107", "Civil Engineering Graphics and Seminars"),
                new Course("PHY1121", "Fundamentals of Physics I"),
                new Course("PHY1122", "Fundamentals of Physics II")

        ));
        populateCourses();
        
    }

    private void populateCourses() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Course course: courseList){
            View courseView = inflater.inflate(R.layout.course_item, coursesContainer,false);

            TextView courseNameTextView = courseView.findViewById(R.id.courseNameTextView);
            TextView courseDescriptionTextView = courseView.findViewById(R.id.courseDescriptionTextView);

            courseNameTextView.setText(course.getName());
            courseDescriptionTextView.setText(course.getDescription());

            coursesContainer.addView(courseView);
        }

    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
