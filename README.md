# Spring Security REST API – My Learning Project

> **Learning Spring Security by building a real project step by step**

This repository contains my **learning journey with Spring Security**.  
I built this project while learning how authentication and authorization work in a Spring Boot REST API.

The goal of this project is **not perfection**, but **understanding**.

---

## Why I Built This Project

I started learning Spring Security and quickly realized that:

- Reading theory was confusing
- Documentation felt overwhelming
- I needed a **hands-on project** to really understand it

So I created this project to:
- Learn Spring Security concepts practically
- Experiment with roles, authentication, and authorization
- Make mistakes, debug them, and learn properly

If you are also learning Spring Security, this project is for you

---

## Tech Stack I Used

- **Java 21**
- **Spring Boot**
- **Spring Security**
- **MySQL**
- **Maven**

This project uses **form-based authentication** and **database-backed users**.

---

## What I Learned From This Project

While building this project, I learned:

- Difference between **authentication** and **authorization**
- How Spring Security protects REST endpoints
- How **roles** work (USER, MANAGER, etc.)
- How passwords are stored securely using **BCrypt**
- How Spring Security decides:
  - Who can log in
  - Who can access which endpoint
- How Spring Security filters requests internally
- How to connect Spring Security with a MySQL database

---

## Authentication vs Authorization (My Understanding)

**Authentication** → *Who are you?*  
**Authorization** → *What are you allowed to do?*

Simple example from this project:

| Concept | Example |
|------|--------|
| Authentication | Login using username and password |
| Authorization | Only MANAGER can access `/approveloan` |

---

## Users and Roles in This Project

I created three types of users:
- VISITOR (ajay)
  - No roles
  - Can access only public endpoints

- USER (akash)
  - ROLE_USER
  - Can check balance and view offers

- MANAGER (som)
  - ROLE_USER
  - ROLE_MANAGER
  - Can approve loans

This helped me clearly understand **role-based access control (RBAC)**.

---

## Password Encryption (BCrypt)

I learned that:

❌ Storing plain text passwords is **very dangerous**  
✅ Passwords should always be **hashed**

Spring Security uses **BCrypt**, which:
- Adds a random salt
- Generates a different hash every time
- Still correctly verifies passwords during login

Even if someone steals the database, they **cannot see real passwords**.

---

## How Spring Security Works (Simplified)

This is how I understood the flow:

```
┌─────────────────────────────────────────────────────────────┐
│                     Client (Browser/Postman)                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Spring Security Filter Chain             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Authentication Filter → Authorization Filter        │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    BankOperationController                  │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  @PreAuthorize Security Checks                       │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              JdbcUserDetailsManager (Spring Security)       │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Load User Details & Roles from Database             │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      MySQL Database                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  users table → authorities table                     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```
- Not logged in → redirect to login / 401
- Logged in but no role → 403 Forbidden
- Logged in with correct role → access allowed ✅

---

## Project Structure (What Each File Does)

```
src/main/java/com/boot/mvc/
│
├── BootMvcBasicsSecurityApplication.java
│ → Main class to start the application
│
├── config/
│ └── SecurityConfig.java
│ → All security rules are defined here
│
├── controller/
│ └── BankOperationController.java
│ → REST API endpoints
│
└── encrypt/
└── PasswordEncoder.java
→ Used to generate BCrypt password hashes
```

---

## API Endpoints I Created

Base URL: http://localhost:4545/bank


| Endpoint | Auth Required | Role |
|--------|--------------|------|
| `/home` | ❌ No | Public |
| `/offers` | ✅ Yes | Any user |
| `/checkBalance` | ✅ Yes | USER / MANAGER |
| `/approveloan` | ✅ Yes | MANAGER |

---

## What I Tested While Learning

- Accessing protected endpoints without login
- Logging in with different users
- Checking role-based access
- Trying wrong passwords
- Understanding **401 vs 403** errors
- Adding new users manually in the database

This helped me **really understand** how Spring Security behaves.

---

## Database Structure I Used

Two tables are used by Spring Security:

### users
```sql
username | password | enabled
```
### authorities
```sql
username | authority
```
---

## How to Run This Project

1. Clone the repository  
2. Create the MySQL database  
3. Update `application.properties` with your database credentials  
4. Run the application  
5. Test endpoints using browser or Postman  

This project runs on **port 4545**.

---

## What I Still Want to Learn Next

This project helped me build a foundation.  
Next topics I plan to learn:

- JWT authentication  
- Stateless APIs  
- OAuth2 (Google login)  
- Method-level security  
- CSRF & CORS  
- OWASP security basics  

---

## Who This Project Is For

- Beginners learning Spring Security  
- Java developers confused by authentication & authorization  
- Anyone who learns better by **building things**  

This is **not a production-ready project** — it’s a **learning project**.

---

## Final Thoughts

Spring Security felt scary at first 😅  
But building this project made it **much clearer**.

If you’re learning:

- Don’t rush  
- Break things  
- Read errors  
- Experiment a lot  

That’s how I learned — and I’m still learning 

---

**Built while learning by Som Gupta ❤️**

⭐ If this repository helps you understand Spring Security, feel free to star it!

