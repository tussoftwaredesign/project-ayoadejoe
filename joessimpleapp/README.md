# Who Wants to Be a Knowledge Bank? 🎓🤪  
A Quiz & Joke Desktop App – OOP2 Java 21 Project

## 📘 About the Project
This is a modular, scalable Java desktop application built as part of the MSc. Software Design with AI OOP2 coursework at TUS. It combines learning with laughter, quizzing the user and rewarding correct or wrong answers with jokes fetched online or served offline.

> Developed by **Adetunji Ayoade**  
> Submitted: April 2025

---

## 🚀 Features Demonstrated

✅ Java 21 Features  
- **Records** (`QuizData`, `JokeData`)  
- **Sealed Classes** (`ScoreEntry`, `ScoreNode`)  
- **Switch Expressions** (used in enum mapping)  
- **Pattern Matching**

✅ Advanced Java  
- **Functional Interfaces** (`Supplier`, `Consumer`, `Function`, `Predicate`)  
- **Streams and Collectors**  
- **Concurrency with ExecutorService**  
- **Generics & Custom Serializer with NIO2**  
- **Recursive Object Structures** (score history)  
- **Enum Usage**  
- **Date/Time API**

---

## 📂 Project Structure

- `concurrent_asyncs/` – Async fetchers and internet checks  
- `events_gui/` – Main Swing GUI logic  
- `enum_oops2/` – Enum classes and optional Java 22 snippet  
- `history/` – Score tracking and serialization logic  
- `nio_fileoperations/` – Custom NIO-based file handler  
- `record_pojos/` – Lightweight record models

---

## 📡 API Endpoints Used
- Trivia API: `https://the-trivia-api.com/v2/questions`  
- Joke API: `https://v2.jokeapi.dev/joke/Any`

---

## 📈 Scalability
This project is built for modular growth. New features (e.g., leaderboard, multiplayer, text-to-speech) can be added without disrupting existing code, thanks to:
- Decoupled services (fetchers, serializers)
- Interface and abstract class-driven architecture
- Clean separation of concerns

---

## 🤝 Contributions
This repository is **public** and open to pull requests. Contributions are welcome!

Fork it, enhance it, and let's make education fun for all 🎯

---

## 📦 How to Run
Requires **JDK 21** and `json-20250107.jar` in classpath.

- Clone repo  
- Compile using Eclipse or `javac`  
- Run `PreLaunch.java` to start the app

---

## 🏁 Final Notes
- App falls back to offline mode if internet is unavailable.
- User quiz performance is saved and serialized for later sessions.

---
