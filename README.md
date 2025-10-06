# JACKPOT-BET-REWARD 🎰

A **Spring Boot** microservice for managing jackpot betting with **Kafka** event streaming, **H2** database, and *
*enterprise-grade** validation.

## 🎯 Project Summary

**JACKPOT-BET-REWARD** is a distributed betting system that handles:

- **Bet Placement**: RESTful API for placing bets with comprehensive validation
- **Jackpot Management**: Multiple jackpot types (Fixed/Variable contribution & reward strategies)
- **Event Streaming**: Kafka-based asynchronous bet processing
- **Reward Evaluation**: Real-time jackpot win determination
- **Caching**: High-performance caching for jackpot data

### Key Features

- ✅ **Enterprise Validation**: Field-level + business rule validation
- ✅ **Builder Pattern**: Fluent object construction
- ✅ **Performance Caching**: Spring Cache with eviction strategies
- ✅ **Strategy Pattern**: Pluggable contribution/reward algorithms
- ✅ **Event-Driven**: Kafka producers/consumers for scalability
- ✅ **Comprehensive Testing**: 191 tests with 100% pass rate

## 🚀 Running the Application

### Prerequisites

- **Java 17+**
- **Docker & Docker Compose** (for Kafka)
- **Git** (for cloning)

### Step 1: Clone the Project

```bash
git clone https://github.com/dyte/jackpot-bet-reward-task.git
cd jackpot-bet-reward-task
```

### Step 2: Environment Setup

```bash
# Verify Java version
java -version

# Verify Docker is running
docker --version
docker-compose --version
```

### Step 3: Start Kafka Cluster

```bash
# Start Kafka and Zookeeper using Docker Compose
docker-compose up -d

# Verify containers are running
docker-compose ps

# Wait for Kafka to be ready (30-60 seconds)
docker-compose logs kafka
```

### Step 4: Build and Run Application

```bash
# Clean and build
./gradlew clean build

# Run the application
./gradlew bootRun

# Application will start on http://localhost:8080
```

### Step 5: Verify H2 Database

```bash
# Access H2 database console
# Open browser: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:jackpotdb
# Username: sa, Password: (empty)
```

### Step 6: Test the API

```bash
# Place a single bet
curl -X POST http://localhost:8080/api/bets/place-bet \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "bet-123",
    "userId": "user-456",
    "jackpotId": 1,
    "betAmount": 100.00
  }'

# Evaluate reward
curl http://localhost:8080/api/rewards/evaluate/bet-123
```

## 🧪 API Testing Examples

### Single Request Examples

**Place a Bet:**

```bash
curl -X POST http://localhost:8080/api/bets/place-bet \
  -H "Content-Type: application/json" \
  -d '{
    "betId": "bet-001",
    "userId": "user-001",
    "jackpotId": 1,
    "betAmount": 100.00
  }'
```

**Evaluate Reward:**

```bash
curl -X GET http://localhost:8080/api/rewards/evaluate/bet-001
```

**Clear Cache:**

```bash
curl -X POST http://localhost:8080/api/rewards/cache/clear
```

**Get Cache Stats:**

```bash
curl -X GET http://localhost:8080/api/rewards/cache/stats
```

### Batch Testing (1-100 Requests)

**Place 100 Bets:**

```bash
# Place 100 bets on Classic Jackpot (ID: 1)
for i in $(seq -w 1 100); do
  curl -X POST http://localhost:8080/api/bets/place-bet \
    -H "Content-Type: application/json" \
    -d "{
      \"betId\": \"bet-$i\",
      \"userId\": \"user-001\",
      \"jackpotId\": 1,
      \"betAmount\": 100.00
    }"
  echo ""
  sleep 0.1
done
```

**Evaluate 100 Rewards:**

```bash
# Check rewards for all 100 bets
for i in $(seq -w 1 100); do
  curl -X GET http://localhost:8080/api/rewards/evaluate/bet-$i
  echo ""
  sleep 0.1
done
```

**Mixed Jackpot Testing:**

```bash
# Place bets on both jackpots
for i in $(seq -w 1 50); do
  # Classic Jackpot (ID: 1)
  curl -X POST http://localhost:8080/api/bets/place-bet \
    -H "Content-Type: application/json" \
    -d "{
      \"betId\": \"classic-$i\",
      \"userId\": \"user-001\",
      \"jackpotId\": 1,
      \"betAmount\": 100.00
    }"

  # Progressive Jackpot (ID: 2)
  curl -X POST http://localhost:8080/api/bets/place-bet \
    -H "Content-Type: application/json" \
    -d "{
      \"betId\": \"progressive-$i\",
      \"userId\": \"user-002\",
      \"jackpotId\": 2,
      \"betAmount\": 50.00
    }"

  echo ""
  sleep 0.1
done
```

**Multiple Reward Evaluations:**

```bash
# Try multiple times to increase win chances
for attempt in {1..5}; do
  echo "=== Attempt $attempt ==="
  for i in $(seq -w 1 10); do
    curl -X GET http://localhost:8080/api/rewards/evaluate/bet-$i
    echo ""
  done
  sleep 2
done
```

## 📊 API Documentation

### Endpoints

| Method | Endpoint                        | Description      |
|--------|---------------------------------|------------------|
| `POST` | `/api/bets/place-bet`           | Place a bet      |
| `GET`  | `/api/rewards/evaluate/{betId}` | Check if bet won |
| `POST` | `/api/rewards/cache/clear`      | Clear all caches |
| `GET`  | `/api/rewards/cache/stats`      | Cache statistics |

### Request/Response Examples

**Place a Bet:**

```json
POST /api/bets/place-bet
{
  "betId": "bet-12345",
  "userId": "user-001",
  "jackpotId": 1,
  "betAmount": 100.0
}

Response: 202 Accepted
{
"betId": "bet-12345",
"message": "Bet accepted and sent for processing",
"status": "ACCEPTED"
}
```

**Evaluate Reward:**

```json
GET /api/rewards/evaluate/bet-12345

Response: 200 OK (Winner)
{
"betId": "bet-12345",
"isWinner": true,
"rewardAmount": 5000.0,
"message": "Congratulations! You won the jackpot!"
}

Response: 200 OK (Not Winner)
{
"betId": "bet-12345",
"isWinner": false,
"rewardAmount": 0,
"message": "Better luck next time!"
}
```

## 🏗️ Architecture

```
┌──────────────┐     ┌─────────────┐     ┌─────────────┐
│   REST API   │────▶│   Kafka     │────▶│  Consumer   │
│ (Bet/Jackpot)│     │  (Events)   │     │ (Processing)│
└──────────────┘     └─────────────┘     └─────────────┘
                                               
┌──────────────┐     ┌──────────────┐
│   H2 DB      │     │   Cache      │
│ (Persistence)│     │ (Performance)│
└──────────────┘     └──────────────┘
```

### System Flow

1. **Bet Placement**: Client sends bet via REST API
2. **Event Publishing**: Bet is published to Kafka topic
3. **Asynchronous Processing**: Kafka consumer processes the bet
4. **Contribution**: Bet amount contributes to jackpot pool
5. **Reward Evaluation**: Client can check if bet wins jackpot

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./gradlew test

# Run specific test suites
./gradlew test --tests "*Controller*"
./gradlew test --tests "*Service*"
./gradlew test --tests "*Validation*"
```

### Test Coverage

- **191 tests** covering unit, integration, and validation scenarios
- **100% pass rate** with comprehensive edge case coverage
- **Mock-based testing** for external dependencies

## 🗄️ Database

### H2 Console Access

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:jackpotdb`
- **Username**: `sa`
- **Password**: (leave empty)

### Key Tables

- **JACKPOT**: Jackpot configurations and current pool values
- **JACKPOT_CONTRIBUTION**: All bet contributions to jackpots
- **JACKPOT_REWARD**: All jackpot wins and rewards

## ⚙️ Configuration

### Default Jackpots

**Jackpot 1 - Classic Jackpot:**

- Initial Pool: $1,000
- Contribution: Fixed 5%
- Reward: Fixed 1% chance

**Jackpot 2 - Progressive Jackpot:**

- Initial Pool: $500
- Contribution: Variable (10% → 2%)
- Reward: Variable (0.5% → 100% at $10,000)

### Application Settings

- **Server Port**: 8080
- **Kafka Topic**: jackpot-bets
- **Kafka Bootstrap**: localhost:9092
- **Database**: H2 in-memory

## 🔧 Troubleshooting

### Common Issues

**Port 8080 already in use:**

```bash
# Find and kill process
lsof -i :8080
kill -9 <PID>

# Or change port in application.yml
server:
  port: 8081
```

**Kafka connection errors:**

```bash
# Check Kafka status
docker-compose ps

# Restart Kafka
docker-compose down
docker-compose up -d
```

**Bet not being processed:**

1. Check application logs for errors
2. Verify Kafka consumer is running
3. Check H2 console for contributions
4. Ensure correct jackpot ID (1 or 2)

## 🏛️ Design Patterns

- **Strategy Pattern**: Contribution and reward calculations
- **Builder Pattern**: Fluent object construction
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: API request/response objects
- **Factory Pattern**: Strategy selection

## 🚀 Future Enhancements

- Authentication and authorization
- User balance management
- Real-time notifications (WebSocket)
- Admin endpoints for jackpot management
- Metrics and monitoring (Prometheus/Grafana)
- API documentation (Swagger/OpenAPI)

---

**Built with**: Spring Boot 3.5.6 • Java 17 • Kafka • H2 • Gradle
