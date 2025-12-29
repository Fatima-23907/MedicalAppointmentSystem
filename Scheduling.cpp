#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <queue>
#include <sstream>
#include <map>

using namespace std;

/* =====================================================
   LEVEL 1: LINKED LIST (Appointment Management)
   ===================================================== */

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
    int autoIncrementId;

public:
    AppointmentLinkedList() : head(nullptr), autoIncrementId(1) {}

    void addAppointment(int patientId, int doctorId,
                        const string& date, const string& timeSlot) {
        AppointmentNode* node = new AppointmentNode{
            autoIncrementId++, patientId, doctorId,
            date, timeSlot, "SCHEDULED", head
        };
        head = node;
    }

    bool isSlotAvailable(int doctorId,
                         const string& date,
                         const string& timeSlot) const {
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

    void saveToFile(const string& filename) const {
        ofstream out(filename);
        AppointmentNode* current = head;

        while (current) {
            out << current->appointmentId << ","
                << current->patientId << ","
                << current->doctorId << ","
                << current->date << ","
                << current->timeSlot << ","
                << current->status << "\n";
            current = current->next;
        }
    }

    void loadFromFile(const string& filename) {
        ifstream in(filename);
        if (!in.is_open()) return;

        string line;
        while (getline(in, line)) {
            stringstream ss(line);
            AppointmentNode* node = new AppointmentNode();
            string temp;

            getline(ss, temp, ','); node->appointmentId = stoi(temp);
            getline(ss, temp, ','); node->patientId = stoi(temp);
            getline(ss, temp, ','); node->doctorId = stoi(temp);
            getline(ss, node->date, ',');
            getline(ss, node->timeSlot, ',');
            getline(ss, node->status, ',');

            node->next = head;
            head = node;

            autoIncrementId = max(autoIncrementId, node->appointmentId + 1);
        }
    }
};

AppointmentLinkedList appointmentList;

/* =====================================================
   LEVEL 2: GRAPH (Doctor Time Slot Availability)
   ===================================================== */

struct TimeSlot {
    string time;
    bool available;
    int doctorId;
};

class TimeSlotGraph {
private:
    map<string, vector<TimeSlot>> scheduleGraph;

    vector<string> defaultSlots() const {
        return {
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        };
    }

public:
    void setupDay(const string& date, int doctorId) {
        for (const string& slot : defaultSlots()) {
            bool free = appointmentList.isSlotAvailable(doctorId, date, slot);
            scheduleGraph[date].push_back({ slot, free, doctorId });
        }
    }

    // BFS-based traversal to list available slots
    vector<string> findAvailableSlots(const string& date, int doctorId) {
        if (!scheduleGraph.count(date)) {
            setupDay(date, doctorId);
        }

        vector<string> available;
        queue<TimeSlot> q;

        for (const auto& slot : scheduleGraph[date]) {
            if (slot.doctorId == doctorId) {
                q.push(slot);
            }
        }

        while (!q.empty()) {
            TimeSlot current = q.front();
            q.pop();

            if (current.available) {
                available.push_back(current.time);
            }
        }
        return available;
    }

    void markBooked(const string& date,
                    const string& timeSlot,
                    int doctorId) {
        for (auto& slot : scheduleGraph[date]) {
            if (slot.time == timeSlot &&
                slot.doctorId == doctorId) {
                slot.available = false;
                return;
            }
        }
    }
};

TimeSlotGraph slotGraph;

/* =====================================================
   FILE-BASED OPERATIONS (GUI SAFE)
   ===================================================== */

const string DATA_PATH = "../DataFiles/";

void scheduleAppointment() {
    ifstream in(DATA_PATH + "schedule_input.txt");
    string line;
    getline(in, line);
    in.close();

    stringstream ss(line);
    string temp, date, timeSlot;
    int patientId, doctorId;

    getline(ss, temp, ','); patientId = stoi(temp);
    getline(ss, temp, ','); doctorId = stoi(temp);
    getline(ss, date, ',');
    getline(ss, timeSlot, ',');

    ofstream out(DATA_PATH + "schedule_output.txt");

    if (appointmentList.isSlotAvailable(doctorId, date, timeSlot)) {
        appointmentList.addAppointment(patientId, doctorId, date, timeSlot);
        slotGraph.markBooked(date, timeSlot, doctorId);

        out << "SUCCESS\n";
        out << "Appointment scheduled on " << date
            << " at " << timeSlot << "\n";
    }
    else {
        out << "ERROR: Time slot not available\n";
    }

    appointmentList.saveToFile(DATA_PATH + "appointments.txt");
}

void getAvailableSlots() {
    ifstream in(DATA_PATH + "slots_query.txt");
    int doctorId;
    string date;

    in >> doctorId;
    in.ignore();
    getline(in, date);
    in.close();

    vector<string> slots =
        slotGraph.findAvailableSlots(date, doctorId);

    ofstream out(DATA_PATH + "available_slots.txt");
    out << "AVAILABLE_SLOTS\n";
    for (const string& slot : slots) {
        out << slot << "\n";
    }
}

void exportAllAppointments() {
    appointmentList.saveToFile(
        DATA_PATH + "all_appointments.txt"
    );
}

/* =====================================================
   MAIN CONTROLLER
   ===================================================== */

int main() {
    appointmentList.loadFromFile(
        DATA_PATH + "appointments.txt"
    );

    ifstream cmd(DATA_PATH + "schedule_command.txt");
    string command;
    getline(cmd, command);

    if (command == "SCHEDULE_APPOINTMENT")
        scheduleAppointment();
    else if (command == "GET_AVAILABLE_SLOTS")
        getAvailableSlots();
    else if (command == "GET_ALL_APPOINTMENTS")
        exportAllAppointments();

    return 0;
}
