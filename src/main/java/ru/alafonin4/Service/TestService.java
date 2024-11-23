package ru.alafonin4.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alafonin4.Entity.*;
import ru.alafonin4.Repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TestService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private UserTestSessionRepository userTestSessionRepository;
    public UserTestSession startTest(Long userId) {
        UserTestSession testSession = new UserTestSession();
        testSession.setUser(userRepository.findById(userId).get());
        List<UserAnswer> userAnswers = new ArrayList<>();
        testSession.setUserAnswers(userAnswers);
        testSession.setStartedAt(LocalDateTime.now());
        userTestSessionRepository.save(testSession);

        return testSession;
    }

    public Question getNextQuestion(UserTestSession testSession) {
        int prevQue = 0;
        if (testSession.getUserAnswers() != null) {
            prevQue = testSession.getUserAnswers().size();
            if (prevQue > 24) {
                return null;
            }
        }
        int numberOfDifficulty = prevQue / 5 + 1;
        List<Question> questions = questionRepository.findByDifficulty(numberOfDifficulty);
        Random random = new Random();

        return questions.get(random.nextInt(questions.size()));
    }

    public void processUserAnswer(Long userId, UserTestSession testSession, Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUser(userRepository.findById(userId).get()); // Используем ID пользователя
        userAnswer.setQuestion(answer.getQuestion());
        userAnswer.setAnswer(answer);
        userAnswer.setIsCorrect(answer.getIsRight());
        userAnswer.setTestSession(testSession);  // Привязываем к текущей сессии

        userAnswerRepository.save(userAnswer);  // Сохраняем ответ
        var l = testSession.getUserAnswers();
        l.add(userAnswer);
        testSession.setUserAnswers(l);
    }

}

