package ru.ulanton.courses.ulanton_courses.util;

public class EmailTemplates {

    private EmailTemplates() {}

    public static String buildAdminMessage(String name, String email, String topic, String message){
        return String.format("""
                НОВОЕ ОБРАЩЕНИЕ С САЙТА ULTON
                
                Имя: %s
                Email: %s
                Тема: %s
                
                Сообщение: 
                %s
                
                ---
                Сообщение отправлено с формы обратной связи  
                """, name, email, topic, message);
    }

    public static String buildUserMessage(String name, String topic){
        return String.format("""
                Уважаемый %s,
                
                Спасибо за обращение в Ulton!
                Мы получили ваше сообщение на тему "%s".
                Наша команда рассмотрит его и свяжется с вами в ближайшее время.
                
                С уважением, 
                Команда Ulton
                """, name, topic);
    }
}
