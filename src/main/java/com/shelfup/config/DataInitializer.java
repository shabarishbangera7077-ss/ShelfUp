package com.shelfup.config;

import com.shelfup.entity.Question;
import com.shelfup.entity.QuestionType;
import com.shelfup.entity.QuizDifficulty;
import com.shelfup.entity.QuizTopic;
import com.shelfup.repository.QuestionRepository;
import com.shelfup.repository.QuizTopicRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer {
    private final QuizTopicRepository quizTopicRepository;
    private final QuestionRepository questionRepository;

    public DataInitializer(QuizTopicRepository quizTopicRepository, QuestionRepository questionRepository) {
        this.quizTopicRepository = quizTopicRepository;
        this.questionRepository = questionRepository;
    }

    @PostConstruct
    public void init() {
        if (quizTopicRepository.count() == 0) {
            List<String> topicNames = List.of("Java", "Python", "SQL", "DSA", "DBMS");
            for (String name : topicNames) {
                QuizTopic topic = new QuizTopic();
                topic.setName(name);
                quizTopicRepository.save(topic);
            }
        }

        if (questionRepository.count() == 0) {
            insertJavaQuestions();
            insertPythonQuestions();
            insertSqlQuestions();
            insertDsaQuestions();
            insertDbmsQuestions();
        }
    }

    private void insertJavaQuestions() {
        QuizTopic topic = quizTopicRepository.findAll().stream().filter(t -> t.getName().equals("Java")).findFirst().orElseThrow();
        List<Question> questions = List.of(
                question(topic, "Java", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which keyword is used to create a class in Java?", "class", "new", "static", "void", "A", "A class is declared using the class keyword."),
                question(topic, "Java", QuizDifficulty.EASY, QuestionType.TECHNICAL, "What is the default value of a boolean in Java?", "null", "0", "false", "undefined", "C", "Boolean fields default to false."),
                question(topic, "Java", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which collection is best for fast random access by index?", "Set", "List", "Map", "Queue", "B", "ArrayList provides indexed access in O(1) time."),
                question(topic, "Java", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What does the final keyword do on a method?", "Makes it abstract", "Prevents overriding", "Makes it static", "Deletes it", "B", "final methods cannot be overridden in subclasses."),
                question(topic, "Java", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which of these is a checked exception?", "NullPointerException", "ArrayIndexOutOfBoundsException", "IOException", "IllegalStateException", "C", "IOException is checked and must be handled or declared."),
                question(topic, "Java", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which access modifier allows access from any class?", "private", "protected", "default", "public", "D", "public provides unrestricted access."),
                question(topic, "Java", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which interface is implemented by all classes in Java?", "Serializable", "Runnable", "Comparable", "Object", "D", "Every class inherits from Object."),
                question(topic, "Java", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which method is called when an object is created?", "initialize()", "start()", "constructor", "main()", "C", "Constructors initialize new instances."),
                question(topic, "Java", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which operator is used to compare values for equality?", "=", "==", "!=", ":=", "B", "== compares primitive values and object references."),
                question(topic, "Java", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What is the purpose of a package in Java?", "To speed execution", "To organize classes", "To store variables", "To create loops", "B", "Packages group related classes and interfaces."));
        questionRepository.saveAll(questions);
    }

    private void insertPythonQuestions() {
        QuizTopic topic = quizTopicRepository.findAll().stream().filter(t -> t.getName().equals("Python")).findFirst().orElseThrow();
        List<Question> questions = List.of(
                question(topic, "Python", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which keyword defines a function in Python?", "func", "def", "function", "lambda", "B", "Functions are defined using def."),
                question(topic, "Python", QuizDifficulty.EASY, QuestionType.TECHNICAL, "What is the output of len([1,2,3])?", "2", "3", "4", "error", "B", "The list has 3 elements."),
                question(topic, "Python", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which collection keeps key-value pairs?", "tuple", "list", "dictionary", "set", "C", "Dictionary maps keys to values."),
                question(topic, "Python", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What does append() do to a list?", "removes item", "adds at end", "sorts", "reverses", "B", "append adds an item to the end."),
                question(topic, "Python", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which statement creates a generator?", "return", "yield", "lambda", "async", "B", "yield pauses execution and returns values one at a time."),
                question(topic, "Python", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which type holds text in Python?", "int", "str", "float", "bool", "B", "str stores strings."),
                question(topic, "Python", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "How do you create a list comprehension?", "[x for x in range(3)]", "list(x)", "{x}", "(x)", "A", "List comprehensions are written in bracket form."),
                question(topic, "Python", QuizDifficulty.HARD, QuestionType.TECHNICAL, "What is the result of 3 ** 2 in Python?", "6", "9", "5", "8", "B", "** is exponentiation."),
                question(topic, "Python", QuizDifficulty.EASY, QuestionType.TECHNICAL, "What does a while loop do?", "Runs once", "Runs until a condition is false", "Compiles code", "Creates functions", "B", "while loops repeat while their condition remains true."),
                question(topic, "Python", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which method converts an integer to a string?", "toString()", "str()", "int()", "format()", "B", "str() converts to string."));
        questionRepository.saveAll(questions);
    }

    private void insertSqlQuestions() {
        QuizTopic topic = quizTopicRepository.findAll().stream().filter(t -> t.getName().equals("SQL")).findFirst().orElseThrow();
        List<Question> questions = List.of(
                question(topic, "SQL", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which SQL statement reads rows from a table?", "INSERT", "SELECT", "DELETE", "UPDATE", "B", "SELECT retrieves rows from a table."),
                question(topic, "SQL", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which clause filters rows?", "GROUP BY", "ORDER BY", "WHERE", "HAVING", "C", "WHERE filters rows before grouping."),
                question(topic, "SQL", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which keyword sorts data in descending order?", "SORT", "ORDER BY DESC", "GROUP BY", "FILTER", "B", "DESC orders descending."),
                question(topic, "SQL", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which command removes rows from a table?", "DROP", "REMOVE", "DELETE", "CLEAR", "C", "DELETE removes records."),
                question(topic, "SQL", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which join returns only matching rows from both tables?", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "FULL JOIN", "C", "INNER JOIN returns only common rows."),
                question(topic, "SQL", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which clause groups rows by a column?", "WHERE", "GROUP BY", "JOIN", "HAVING", "B", "GROUP BY aggregates rows by one or more columns."),
                question(topic, "SQL", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What does DISTINCT do?", "Filters duplicates", "Deletes rows", "Creates index", "Groups data", "A", "DISTINCT removes duplicate rows."),
                question(topic, "SQL", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which SQL command creates a new table?", "ALTER", "CREATE TABLE", "INSERT", "UPDATE", "B", "CREATE TABLE defines a new table."),
                question(topic, "SQL", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which key uniquely identifies records in a table?", "Foreign key", "Primary key", "Unique index", "Index", "B", "Primary key uniquely identifies each row."),
                question(topic, "SQL", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which condition checks whether a value is between two ranges?", "IN", "LIKE", "BETWEEN", "EXISTS", "C", "BETWEEN checks range inclusion."));
        questionRepository.saveAll(questions);
    }

    private void insertDsaQuestions() {
        QuizTopic topic = quizTopicRepository.findAll().stream().filter(t -> t.getName().equals("DSA")).findFirst().orElseThrow();
        List<Question> questions = List.of(
                question(topic, "DSA", QuizDifficulty.EASY, QuestionType.TECHNICAL, "What is the time complexity of binary search on sorted data?", "O(n)", "O(log n)", "O(n log n)", "O(1)", "B", "Binary search halves the search space each step."),
                question(topic, "DSA", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which data structure uses FIFO order?", "Stack", "Queue", "Tree", "HashMap", "B", "Queue follows First In First Out."),
                question(topic, "DSA", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which traversal visits root before children?", "Inorder", "Postorder", "Preorder", "Level order", "C", "Preorder visits root then left then right."),
                question(topic, "DSA", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What is a hash table mainly used for?", "Sorting", "Fast lookup", "Rendering", "Compression", "B", "Hash tables offer average O(1) lookup."),
                question(topic, "DSA", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which sorting algorithm has worst-case O(n^2) time?", "Merge sort", "Quick sort", "Heap sort", "Bubble sort", "D", "Bubble sort is O(n^2) in worst case."),
                question(topic, "DSA", QuizDifficulty.EASY, QuestionType.TECHNICAL, "A stack is based on which principle?", "FIFO", "LIFO", "Random access", "Round robin", "B", "LIFO means last item in is first out."),
                question(topic, "DSA", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which structure is best for representing a hierarchical tree?", "Array", "Linked list", "Node-based structure", "Queue", "C", "Tree nodes recursively connect parent and child relationships."),
                question(topic, "DSA", QuizDifficulty.HARD, QuestionType.TECHNICAL, "What is the average time complexity of inserting in a balanced binary search tree?", "O(1)", "O(log n)", "O(n)", "O(n log n)", "B", "Balanced BST insertion is logarithmic."),
                question(topic, "DSA", QuizDifficulty.EASY, QuestionType.TECHNICAL, "What is the top element of a stack called?", "Base", "Peek", "Root", "Tail", "B", "Peek reads the top element without removing it."),
                question(topic, "DSA", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which algorithm is used to find shortest paths in a weighted graph?", "DFS", "Dijkstra", "BFS", "Selection sort", "B", "Dijkstra computes shortest paths from a source."));
        questionRepository.saveAll(questions);
    }

    private void insertDbmsQuestions() {
        QuizTopic topic = quizTopicRepository.findAll().stream().filter(t -> t.getName().equals("DBMS")).findFirst().orElseThrow();
        List<Question> questions = List.of(
                question(topic, "DBMS", QuizDifficulty.EASY, QuestionType.TECHNICAL, "What does DBMS stand for?", "Database Management System", "Data Backup Method Service", "Dynamic Binary Mapping Source", "Database Method Storage", "A", "DBMS manages databases and data access."),
                question(topic, "DBMS", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which key enforces referential integrity?", "Primary key", "Foreign key", "Alternate key", "Unique key", "B", "Foreign keys point to related primary keys in another table."),
                question(topic, "DBMS", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "Which normal form removes partial dependency?", "1NF", "2NF", "3NF", "BCNF", "B", "2NF removes partial dependency."),
                question(topic, "DBMS", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What is ACID?", "Atomicity, Consistency, Isolation, Durability", "Access, Control, Index, Data", "Alignment, Check, Integrity, Database", "Algorithm, Code, Index, Data", "A", "ACID properties guarantee reliable transactions."),
                question(topic, "DBMS", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which join returns unmatched rows from both tables?", "LEFT JOIN", "INNER JOIN", "FULL OUTER JOIN", "RIGHT JOIN", "C", "FULL OUTER JOIN includes all unmatched rows."),
                question(topic, "DBMS", QuizDifficulty.EASY, QuestionType.TECHNICAL, "A transaction is a unit of what?", "Sorting", "Work", "Parsing", "Rendering", "B", "Transactions group database actions as one unit."),
                question(topic, "DBMS", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What is an index used for?", "Saving disk space", "Speeding retrieval", "Removing storage", "Encrypting data", "B", "Indexes improve lookup speed."),
                question(topic, "DBMS", QuizDifficulty.HARD, QuestionType.TECHNICAL, "Which form addresses transitive dependency?", "1NF", "2NF", "3NF", "4NF", "C", "3NF removes transitive dependencies."),
                question(topic, "DBMS", QuizDifficulty.EASY, QuestionType.TECHNICAL, "Which command saves changes permanently?", "COMMIT", "ROLLBACK", "DELETE", "SELECT", "A", "COMMIT saves a transaction."),
                question(topic, "DBMS", QuizDifficulty.MEDIUM, QuestionType.TECHNICAL, "What is a view in a database?", "A stored table", "A virtual table", "A trigger", "A schema", "B", "A view is derived from query results and acts like a table."));
        questionRepository.saveAll(questions);
    }

    private Question question(QuizTopic topic, String topicName, QuizDifficulty difficulty, QuestionType type,
                              String questionText, String optionA, String optionB, String optionC, String optionD,
                              String correctOption, String explanation) {
        Question question = new Question();
        question.setTopic(topic);
        question.setDifficulty(difficulty);
        question.setType(type);
        question.setQuestionText(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectOption(correctOption);
        question.setExplanation(explanation);
        question.setApproved(true);
        return question;
    }
}
