package com.example.finalproject.ConfigSecurity;

import com.example.finalproject.Service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ConfigSecurity {

    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(myUserDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() // Disable CSRF for testing purposes (not for production!)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests()
                //Auth
                //get All users
                .requestMatchers("/api/v1/auth/get").hasAuthority("ADMIN")
                //delete user
                .requestMatchers("/api/v1/auth/delete/{user_id}").hasAuthority("ADMIN")

                //Tutor
                .requestMatchers("/api/v1/tutor/get").permitAll()
                //Tutor register
                .requestMatchers("/api/v1/tutor/register").permitAll()
                .requestMatchers("/api/v1/tutor/update").hasAuthority("TUTOR")
                .requestMatchers("/api/v1/tutor/delete").hasAuthority("TUTOR")
                //getAllTutorsWithRecommendations
                .requestMatchers("/api/v1/tutor/tutorsWithRecommendations").permitAll()

                //Certificates
                .requestMatchers("/api/v1/certificate/get-all").hasAuthority("ADMIN")
                .requestMatchers("/api/v1/certificate/get-my").hasAuthority("TUTOR")
                .requestMatchers("/api/v1/certificate/add").hasAuthority("ADMIN")
                .requestMatchers("/api/v1/certificate/update/{certificate_id}").hasAuthority("ADMIN") //??
                .requestMatchers("/api/v1/certificate/delete/{certificate_id}").hasAuthority("ADMIN") //??
                .requestMatchers("/api/v1/certificate/issueCertificate/{certificate_id}").hasAuthority("TUTOR")

                //Student
                .requestMatchers("/api/v1/student/get").hasAnyAuthority("ADMIN", "TUTOR") //+tutor??
                .requestMatchers("/api/v1/student/register").permitAll()
                .requestMatchers("/api/v1/student/update").hasAuthority("STUDENT")
                .requestMatchers("/api/v1/student/delete").hasAuthority("STUDENT")
                .requestMatchers("/api/v1/student/studentEnrollment/{course_id}").hasAnyAuthority("STUDENT", "ADMIN")


                //Course
                .requestMatchers("/api/v1/course/get").permitAll()
                .requestMatchers("/api/v1/course/add").hasAnyAuthority("TUTOR", "ADMIN")
                .requestMatchers("/api/v1/course/update/{course_id}").hasAnyAuthority("TUTOR", "ADMIN")
                .requestMatchers("/api/v1/course/delete/{course_id}").hasAnyAuthority("TUTOR", "ADMIN")
                .requestMatchers("/api/v1/course/courseFilter/{minPrice}/{maxPrice}").permitAll()
                .requestMatchers("api/v1/course/findCoursesByLearningMethod/{learningMethod}").permitAll()
                .requestMatchers("api/v1/course/mostPopularCourses").permitAll()

                .requestMatchers("/api/v1/document/add/{session_id}").hasAnyAuthority("TUTOR", "ADMIN")
                .requestMatchers("/api/v1/document/assignDocumentToStudent/{doc_id}").hasAnyAuthority("STUDENT","ADMIN")


                .requestMatchers("/api/v1/exam/assignExamToTutor/{exam_id}").hasAnyAuthority("TUTOR", "ADMIN")

                .requestMatchers("/api/v1/orders/applyDiscount/applyDiscount/{order_id}").hasAuthority("ADMIN")

                .requestMatchers("/api/v1/review/assignReviewToTutor/{tutor_id}").hasAuthority("STUDENT")
                .requestMatchers("/api/v1/review/assignReviewToCourse/{course_id}").hasAuthority("STUDENT")

                .requestMatchers("/api/v1/session/add/{course_id}").hasAnyAuthority("ADMIN","TUTOR")
                .requestMatchers("/api/v1/session/assignSessionToTutor/{session_id}").hasAnyAuthority("TUTOR", "ADMIN")
                .requestMatchers("/api/v1/session/assignSessionToStudent/{session_id}").hasAnyAuthority("STUDENT","ADMIN")

                .requestMatchers("/api/v1/zoom/add/{session_id}").hasAnyAuthority("TUTOR","ADMIN")
                .requestMatchers("/api/v1/zoom/assignZoomToStudent/{zoom_id}").hasAnyAuthority("STUDENT","ADMIN")

                .requestMatchers("/api/v1/video/add/{session_id}").hasAnyAuthority("TUTOR","ADMIN")

                .requestMatchers("/api/v1/face/AssignFaceMeetingToSession/{session_id}").hasAnyAuthority("TUTOR","ADMIN")


                //Omar
                // Document permissions
                .requestMatchers("/api/v1/document/get").hasAnyAuthority("STUDENT", "TUTOR", "ADMIN")
                .requestMatchers("/api/v1/document/update/", "/api/v1/document/delete/").hasAuthority("TUTOR")
                // Session permissions
                .requestMatchers( "/api/v1/session/update/", "/api/v1/session/delete/", "/api/v1/session/start/", "/api/v1/session/cancel/", "/api/v1/session/end/", "/api/v1/session/block/")
                .hasAuthority("TUTOR") // Only tutors can manage sessions
                .requestMatchers("/api/v1/session/get").hasAnyAuthority( "ADMIN","TUTOR")//reema
                .requestMatchers("/api/v1/session/assign/**").hasAnyAuthority("ADMIN", "TUTOR")
                .requestMatchers( "/api/v1/session/students/", "/api/v1/session/max-participants/")
                .hasAnyAuthority("TUTOR", "STUDENT") // Students can view session details

                // Face-to-Face permissions
                .requestMatchers("/api/v1/face/").hasAnyAuthority("TUTOR","ADMIN")

                // Video permissions
                .requestMatchers("/api/v1/video/add/", "/api/v1/video/update/", "/api/v1/video/delete/", "/api/v1/video/increase-price/", "/api/v1/video/decrease-price/", "/api/v1/video/delete-by-course/")
                .hasAuthority("TUTOR") // Only tutors can manage videos
                .requestMatchers("/api/v1/video/get/", "/api/v1/video/price-range/", "/api/v1/video/search/")
                .hasAnyAuthority("TUTOR", "STUDENT") // Both tutors and students can view and search videos

                // Zoom permissions
                .requestMatchers("/api/v1/zoom/add/", "/api/v1/zoom/update/", "/api/v1/zoom/delete/", "/api/v1/zoom/assignZoomToStudent/")
                .hasAuthority("TUTOR") // Only tutors can manage Zoom meetings
                .requestMatchers("/api/v1/zoom/get/").hasAnyAuthority("TUTOR", "STUDENT") // Both tutors and students can view Zoom meetings

                        .anyRequest().authenticated() // Ensure all other requests require authentication
                        .and()
                        .logout().logoutUrl("/api/v1/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .and()
                        .httpBasic();

       return http.build();
    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable() // Disable CSRF for testing purposes (not for production!)
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll() // Allow all requests to be accessed
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authenticationProvider(daoAuthenticationProvider())
//                .httpBasic(); // Use basic authentication (for testing purposes)
//        return http.build();
//    }
}