#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <queue>
#include <sstream>
#include <map>
using namespace std;

// ===== LEVEL-1 DSA: LINKED LIST =====
struct AppointmentNode {
    int appointmentId;
    int patientId;
    int doctorId;
    string date;
    string timeSlot;
    string status;
    AppointmentNode* next;
};

class AppointmentLinkedList {
private:
    AppointmentNode* head;
    int nextId;

public:
    AppointmentLinkedList() {
        head = nullptr;
        nextId = 1;
    }

    void addAppointment(int patientId, int doctorId, string date, string timeSlot) {
        AppointmentNode* newNode = new AppointmentNode();
        newNode->appointmentId = nextId++;
        newNode->patientId = patientId;
        newNode->doctorId = doctorId;
        newNode->date = date;
        newNode->timeSlot = timeSlot;
        newNode->status = "SCHEDULED";
        newNode->next = head;
        head = newNode;
    }

    void saveToFile(string filename) {
        ofstream outFile(filename);
        AppointmentNode* current = head;
        while (current) {
            outFile << current->appointmentId << ","
                << current->patientId << ","
                << current->doctorId << ","
                << current->date << ","
                << current->timeSlot << ","
                << current->status << "\n";
            current = current->next;
        }
        outFile.close();
    }

    bool isSlotAvailable(int doctorId, string date, string timeSlot) {
        AppointmentNode* current = head;
        while (current) {
            if (current->doctorId == doctorId &&
                current->date == date &&
                current->timeSlot == timeSlot &&
                current->status == "SCHEDULED") {
                return false;
            }
            current = current->next;
        }
        return true;
    }

    void loadFromFile(string filename) {
        ifstream inFile(filename);
        if (!inFile.is_open()) return;

        string line;
        while (getline(inFile, line)) {
            stringstream ss(line);
            AppointmentNode* newNode = new AppointmentNode();
            string temp;

            getline(ss, temp, ','); newNode->appointmentId = stoi(temp);
            getline(ss, temp, ','); newNode->patientId = stoi(temp);
            getline(ss, temp, ','); newNode->doctorId = stoi(temp);
            getline(ss, newNode->date, ',');
            getline(ss, newNode->timeSlot, ',');
            getline(ss, newNode->status, ',');

            newNode->next = head;
            head = newNode;

            if (newNode->appointmentId >= nextId) {
                nextId = newNode->appointmentId + 1;
            }
        }
        inFile.close();
    }
};

AppointmentLinkedList appointmentList;

// ===== LEVEL-2 DSA: GRAPH (Time Slot Availability) =====
struct TimeSlot {
    string time;
    bool available;
    int doctorId;
};

class TimeSlotGraph {
private:
    map<string, vector<TimeSlot>> adjacencyList; // date -> time slots

public:
    void initializeDay(string date, int doctorId) {
        vector<string> timeSlots = {
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        };

        for (const string& slot : timeSlots) {
            if (appointmentList.isSlotAvailable(doctorId, date, slot)) {
                adjacencyList[date].push_back({ slot, true, doctorId });
            }
            else {
                adjacencyList[date].push_back({ slot, false, doctorId });
            }
        }
    }

    // BFS to find available slots
    vector<string> findAvailableSlots(string date, int doctorId) {
        vector<string> available;

        if (adjacencyList.find(date) == adjacencyList.end()) {
            initializeDay(date, doctorId);
        }

        // BFS traversal through time slots
        queue<TimeSlot> bfsQueue;
        for (const auto& slot : adjacencyList[date]) {
            if (slot.doctorId == doctorId) {
                bfsQueue.push(slot);
            }
        }

        while (!bfsQueue.empty()) {
            TimeSlot current = bfsQueue.front();
            bfsQueue.pop();

            if (current.available) {
                available.push_back(current.time);
            }
        }

        return available;
    }

    void markSlotBooked(string date, string timeSlot, int doctorId) {
        for (auto& slot : adjacencyList[date]) {
            if (slot.time == timeSlot && slot.doctorId == doctorId) {
                slot.available = false;
                break;
            }
        }
    }
};

TimeSlotGraph timeGraph;

// ===== OPERATIONS =====
void scheduleAppointment() {
    ifstream inFile("../DataFiles/schedule_input.txt");
    string line;
    getline(inFile, line);
    inFile.close();

    stringstream ss(line);
    string temp;
    int patientId, doctorId;
    string date, timeSlot;

    getline(ss, temp, ','); patientId = stoi(temp);
    getline(ss, temp, ','); doctorId = stoi(temp);
    getline(ss, date, ',');
    getline(ss, timeSlot, ',');

    ofstream outFile("../DataFiles/schedule_output.txt");

    if (appointmentList.isSlotAvailable(doctorId, date, timeSlot)) {
        appointmentList.addAppointment(patientId, doctorId, date, timeSlot);
        timeGraph.markSlotBooked(date, timeSlot, doctorId);

        outFile << "SUCCESS\n";
        outFile << "Appointment scheduled for " << date << " at " << timeSlot << "\n";
    }
    else {
        outFile << "ERROR: Time slot not available\n";
    }
    outFile.close();

    appointmentList.saveToFile("../DataFiles/appointments.txt");
}

void getAvailableSlots() {
    ifstream inFile("../DataFiles/slots_query.txt");
    string date;
    int doctorId;
    inFile >> doctorId;
    inFile.ignore();
    getline(inFile, date);
    inFile.close();

    vector<string> availableSlots = timeGraph.findAvailableSlots(date, doctorId);

    ofstream outFile("../DataFiles/available_slots.txt");
    outFile << "AVAILABLE_SLOTS\n";
    for (const string& slot : availableSlots) {
        outFile << slot << "\n";
    }
    outFile.close();
}

void getAllAppointments() {
    appointmentList.saveToFile("../DataFiles/all_appointments.txt");
}

int main() {
    appointmentList.loadFromFile("../DataFiles/appointments.txt");

    ifstream commandFile("../DataFiles/schedule_command.txt");
    string command;
    getline(commandFile, command);
    commandFile.close();

    if (command == "SCHEDULE_APPOINTMENT") {
        scheduleAppointment();
    }
    else if (command == "GET_AVAILABLE_SLOTS") {
        getAvailableSlots();
    }
    else if (command == "GET_ALL_APPOINTMENTS") {
        getAllAppointments();
    }

    return 0;
}