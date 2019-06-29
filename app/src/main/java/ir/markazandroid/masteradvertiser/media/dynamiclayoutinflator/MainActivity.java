package ir.markazandroid.masteradvertiser.media.dynamiclayoutinflator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import ir.markazandroid.masteradvertiser.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getLayoutInflater().inflate();
        //XmlPullParserFactory factory = null;
        //getLayoutInflater().inflate()
        //XmlPullParser ps=getResources().getLayout(R.layout.test);
        // ps.getText()

        //try {
        //factory = XmlPullParserFactory.newInstance();
        //factory.setNamespaceAware(true);
        //XmlPullParser  parser = factory.newPullParser();
        // Xml.newPullParser()

        //TextView textView = new TextView(this,Xml.asAttributeSet();

        // } catch (XmlPullParserException e) {
        //    e.printStackTrace();
        // }


       /* setContentView(R.layout.activity_main);
        RelativeLayout main = findViewById(R.id.main_top);
        try {
            View view = DynamicLayoutInflator.inflate(this, getAssets().open("testlayout.xml"), main);
            DynamicLayoutInflator.setDelegate(view, this);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //View view=DynamicLayoutInflator.inflate(this, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><android.support.constraint.ConstraintLayout xmlns:android=\"http://schemas.android.com/apk/res/android\" xmlns:app=\"http://schemas.android.com/apk/res-auto\" android:layout_height=\"match_parent\" android:layout_width=\"match_parent\"><android.support.constraint.Guideline android:id=\"vertical_33\" android:layout_height=\"match_parent\" android:layout_width=\"match_parent\" android:orientation=\"vertical\" app:layout_constraintGuide_percent=\"0.33\"/><android.support.constraint.Guideline android:id=\"vertical_66\" android:layout_height=\"match_parent\" android:layout_width=\"match_parent\" android:orientation=\"vertical\" app:layout_constraintGuide_percent=\"0.66\"/><android.support.constraint.Guideline android:id=\"horizontal_33\" android:layout_height=\"match_parent\" android:layout_width=\"match_parent\" android:orientation=\"horizontal\" app:layout_constraintGuide_percent=\"0.33\"/><android.support.constraint.Guideline android:id=\"horizontal_66\" android:layout_height=\"match_parent\" android:layout_width=\"match_parent\" android:orientation=\"horizontal\" app:layout_constraintGuide_percent=\"0.66\"/><ImageView android:id=\"vertical_33_horizontal_33_vertical_66_horizontal_66\" android:layout_height=\"0dp\" android:layout_width=\"0dp\" app:layout_constraintBottom_toTopOf=\"horizontal_66\" app:layout_constraintLeft_toRightOf=\"vertical_33\" app:layout_constraintRight_toLeftOf=\"vertical_66\" app:layout_constraintTop_toBottomOf=\"horizontal_33\"/><ImageView android:id=\"vertical_66_horizontal_66_vertical_100_horizontal_100\" android:layout_height=\"0dp\" android:layout_width=\"0dp\" app:layout_constraintBottom_toBottomOf=\"parent\" app:layout_constraintLeft_toRightOf=\"vertical_66\" app:layout_constraintRight_toRightOf=\"parent\" app:layout_constraintTop_toBottomOf=\"horizontal_66\"/><ImageView android:id=\"vertical_0_horizontal_0_vertical_33_horizontal_33\" android:layout_height=\"0dp\" android:layout_width=\"0dp\" app:layout_constraintBottom_toTopOf=\"horizontal_33\" app:layout_constraintLeft_toLeftOf=\"parent\" app:layout_constraintRight_toLeftOf=\"vertical_33\" app:layout_constraintTop_toTopOf=\"parent\"/></android.support.constraint.ConstraintLayout>\n");
        //view=null;
        setContentView(/*R.layout.test*/R.layout.main);
        FrameLayout frameLayout = /*new FrameLayout(this);*/ findViewById(R.id.main_container);

        View view2 = DynamicLayoutInflator.inflate(this, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<android.support.constraint.ConstraintLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"match_parent\">\n" +
                        "\n" +
                        "    <android.support.constraint.Guideline\n" +
                        "        android:id=\"@+id/vertical_33\"\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"match_parent\"\n" +
                        "        android:orientation=\"vertical\"\n" +
                        "        app:layout_constraintGuide_percent=\"0.33\" />\n" +
                        "\n" +
                        "    <android.support.constraint.Guideline\n" +
                        "        android:id=\"@+id/vertical_66\"\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"match_parent\"\n" +
                        "        android:orientation=\"vertical\"\n" +
                        "        app:layout_constraintGuide_percent=\"0.66\" />\n" +
                        "\n" +
                        "    <android.support.constraint.Guideline\n" +
                        "        android:id=\"@+id/horizontal_33\"\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"match_parent\"\n" +
                        "        android:orientation=\"horizontal\"\n" +
                        "        app:layout_constraintGuide_percent=\"0.33\" />\n" +
                        "\n" +
                        "    <android.support.constraint.Guideline\n" +
                        "        android:id=\"@+id/horizontal_66\"\n" +
                        "        android:layout_width=\"match_parent\"\n" +
                        "        android:layout_height=\"match_parent\"\n" +
                        "        android:orientation=\"horizontal\"\n" +
                        "        app:layout_constraintGuide_percent=\"0.66\" />\n" +
                        "\n" +
                        "    <ImageView\n" +
                        "        android:id=\"@+id/vertical_33_horizontal_33_vertical_66_horizontal_66\"\n" +
                        "        android:layout_width=\"0dp\"\n" +
                        "        android:layout_height=\"0dp\"\n" +
                        "        android:background=\"#000000\"\n" +
                        "        app:layout_constraintBottom_toTopOf=\"@id/horizontal_66\"\n" +
                        "        app:layout_constraintLeft_toRightOf=\"@id/vertical_33\"\n" +
                        "        app:layout_constraintRight_toLeftOf=\"@id/vertical_66\"\n" +
                        "        app:layout_constraintTop_toBottomOf=\"@id/horizontal_33\" />\n" +
                        "\n" +
                        "    <ImageView\n" +
                        "        android:id=\"@+id/vertical_66_horizontal_66_vertical_100_horizontal_100\"\n" +
                        "        android:layout_width=\"0dp\"\n" +
                        "        android:layout_height=\"0dp\"\n" +
                        "        android:background=\"#000000\"\n" +
                        "        app:layout_constraintBottom_toBottomOf=\"parent\"\n" +
                        "        app:layout_constraintLeft_toRightOf=\"@id/vertical_66\"\n" +
                        "        app:layout_constraintRight_toRightOf=\"parent\"\n" +
                        "        app:layout_constraintTop_toBottomOf=\"@id/horizontal_66\" />\n" +
                        "\n" +
                        "    <ImageView\n" +
                        "        android:id=\"@+id/vertical_0_horizontal_0_vertical_33_horizontal_33\"\n" +
                        "        android:layout_width=\"0dp\"\n" +
                        "        android:layout_height=\"0dp\"\n" +
                        "        android:background=\"#000000\"\n" +
                        "        app:layout_constraintBottom_toTopOf=\"@id/horizontal_33\"\n" +
                        "        app:layout_constraintLeft_toLeftOf=\"parent\"\n" +
                        "        app:layout_constraintRight_toLeftOf=\"@id/vertical_33\"\n" +
                        "        app:layout_constraintTop_toTopOf=\"parent\" />\n" +
                        "</android.support.constraint.ConstraintLayout>\n"
                , frameLayout);

        //view.invalidate();
        View view = findViewById(0);
       // ((ConstraintLayout.LayoutParams) view2.findViewById(DynamicLayoutInflator.idNumFromIdString(view2, "vertical_66_horizontal_66_vertical_100_horizontal_100"))
       //         .getLayoutParams()).bottomToTop = DynamicLayoutInflator.idNumFromIdString(view2, "vertical_0_horizontal_0_vertical_33_horizontal_33");
        view = null;

        //ConstraintLayout

        //frameLayout.addView(view);
        //AttributeSet attributeSet = Resources.getSystem().getLayout(R.layout.test);
        //View view =LayoutInflater.from(appContext).createView("android.support.constraint.ConstraintLayout",null,Xml.asAttributeSet(ps));
    }


    public void ohHello() {
        Log.d("nick", "howdy");
    }

    public void logStringNum(String text, int num) {
        Log.d("nick", "logging: " + text + " - " + num);
    }

}
