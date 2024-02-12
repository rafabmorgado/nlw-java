package dev.morgadorafa.certification_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.morgadorafa.certification_nlw.modules.questions.entities.QuestionEntity;
import dev.morgadorafa.certification_nlw.modules.questions.repositories.QuestionRepository;
import dev.morgadorafa.certification_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import dev.morgadorafa.certification_nlw.modules.students.dto.VerifyHasCertificationDTO;
import dev.morgadorafa.certification_nlw.modules.students.entities.AnswersCertificationsEntity;
import dev.morgadorafa.certification_nlw.modules.students.entities.CertificationStudentEntity;
import dev.morgadorafa.certification_nlw.modules.students.entities.StudentEntity;
import dev.morgadorafa.certification_nlw.modules.students.repositories.CertificationStudentRepository;
import dev.morgadorafa.certification_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {
    
    @Autowired
    private StudentRepository studentRepository;

    @Autowired 
    private QuestionRepository questionRepository;

    @Autowired
    private CertificationStudentRepository certificationStudentRepository;

    @Autowired
    private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;
    
    public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

        var hasCertification = this.verifyIfHasCertificationUseCase.execute(new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology())); 

        if(hasCertification) {
            throw new Exception("Você já tirou essa certificação");
        }

        // Buscar alternativas das perguntas

        List<QuestionEntity> questionsEntity = questionRepository.findByTechnology(dto.getTechnology());
        List<AnswersCertificationsEntity> answersCertifications = new ArrayList<>();

        AtomicInteger correctAnswers = new AtomicInteger(0);

        // Correto ou incorreto
        dto.getQuestionsAnswers()
        .stream().forEach(questionAnswer -> {
            var question = questionsEntity.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionID()))
            .findFirst().get();

            var findCorrectAlternative = question.getAlternatives().stream()
            .filter(alternative -> alternative.isCorrect())
            .findFirst().get();

            if(findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
                questionAnswer.setCorrect(true);
                correctAnswers.incrementAndGet();
            } else {
                questionAnswer.setCorrect(false);
            }
        
            var answersCertificationEntity = AnswersCertificationsEntity.builder()
            .answerID(questionAnswer.getAlternativeID())
            .questionID(questionAnswer.getQuestionID())
            .isCorrect(questionAnswer.isCorrect())
            .build();

            answersCertifications.add(answersCertificationEntity);

        });

        //Verificar se o student existe pelo email

        var student = studentRepository.findByEmail(dto.getEmail());
        UUID studentID;
        if(student.isEmpty()) {
            var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
            studentCreated = studentRepository.save(studentCreated);
            studentID = studentCreated.getId();
        } else {
            studentID = student.get().getId();
        }

        

        CertificationStudentEntity certificationStudentEntity = 
            CertificationStudentEntity.builder()
            .technology(dto.getTechnology())
            .studentID(studentID)
            .grate(correctAnswers.get())
            //.answersCertificationsEntities(answersCertification)
            .build();

        var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

        answersCertifications.stream().forEach(answersCertification -> {
            answersCertification.setCertificationID(certificationStudentEntity.getId());
            answersCertification.setCertificationStudentEntity(certificationStudentEntity);
        });

        certificationStudentRepository.save(certificationStudentEntity);

        certificationStudentEntity.setAnswersCertificationsEntities(answersCertifications);

        return certificationStudentCreated;
        // Salvar as informações da certificação
    }


}
