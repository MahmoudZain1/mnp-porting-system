# MNP (Mobile Number Portability) System

This project is an implementation of the Mobile Number Portability (MNP) system for Egyptian telecom operators (Vodafone, Orange, and Etisalat), developed using Spring Boot, MySQL, and Angular.

---
### 💡 Core Concept
Mobile Number Portability (MNP) allows subscribers to keep their existing phone numbers when switching from their current network operator (**Donor**) to a new one (**Recipient**) without changing their number.
The system manages this end-to-end workflow:
- **Porting Requests:** The recipient operator submits a porting request for a subscriber number (`010`, `011`, or `012`).
- **Donor Approval:** The donor operator verifies subscriber data and either **Accepts** or **Rejects** the request.
- **Automated Expiration:** Requests left in `PENDING` status for more than **2 minutes** are automatically cancelled by a background scheduler.
- **Status Lookup:** Anyone can query the real-time portability status and active network of any mobile number.
- **Visibility Rules:** Operators can view their own porting orders, while third-party operators can only view completed (Accepted) transfers.
---

## 📸 Web Portal Preview

![MNP Portal](/portal.png)

---

## ⚙️ How to Build and Run

### 1. Clone the Repository

Clone the project to your local machine:

```bash
git clone https://github.com/MahmoudZain1/mnp-porting-system.git
cd mnp-porting-system
```

---

### 2. Run with Docker Compose (Recommended)

Make sure Docker and Docker Compose are installed and running on your machine, then execute:

```bash
docker compose up --build -d
```

- Spring Boot API: Starts on http://localhost:9090 (using the prod profile).
- MySQL Database: Starts on port 3306 (or 3307 externally) and automatically initializes tables, indexes, and initial operator data using mnp-init.sql.

To view application logs:
```bash
docker compose logs -f app
```

To stop the containers:
```bash
docker compose down
```

---

### 3. Run the Frontend (Angular)

Ensure Node.js and npm are installed. In a new terminal window, navigate to the frontend directory:

```bash
cd frontend
npm install
npm start
```

Open your browser and navigate to:
http://localhost:4200

---

### 4. Running Tests

To run the automated unit and integration tests:

```bash
./mvnw clean test
```