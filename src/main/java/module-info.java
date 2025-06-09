module com.example.myapp.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires java.xml.crypto;


    opens com.example.myapp.demo1 to javafx.fxml;
    exports com.example.myapp.demo1;
}