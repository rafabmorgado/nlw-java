package dev.morgadorafa.certification_nlw.modules.students.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.morgadorafa.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import dev.morgadorafa.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import dev.morgadorafa.certification_nlw.modules.students.entities.CertificationStudentEntity;
import dev.morgadorafa.certification_nlw.modules.students.useCases.StudentCertificationAnswersUseCase;
import dev.morgadorafa.certification_nlw.modules.students.useCases.VerifyIfHasCertificationUseCase;

@RestController
@RequestMapping("/students")
public class StudentController {

    //implementando o useCase
    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

    @Autowired
    private StudentCertificationAnswersUseCase studentCertificationAnswersUseCase;
    
    @PostMapping("verifyIfHasCertification")
    public String verifyIfHasCertification(@RequestBody VerifyHasCertificationDTO verifyHasCertificationDTO){

        var result =  this.verifyIfHasCertificationUseCase.execute(verifyHasCertificationDTO);
        if (result) {
            return "Usuario ja fez a prova";
        }
        return "Usuario pode fazer a prova";
    }

    @PostMapping("/certification/answer")
    public ResponseEntity<Object> certificationAnswer (@RequestBody StudentCertificationAnswerDTO studentCertificationAnswerDTO) {
        try {
            var result =  this.studentCertificationAnswersUseCase.execute(studentCertificationAnswerDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        
    }

}
