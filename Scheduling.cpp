#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <queue>
#include <sstream>
#include <map>

using namespace std;

/* =====================================================
   DATA STRUCTURE: LINKED LIST
   PURPOSE: Store & manage appointments
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

class AppointmentManager {
private:
    AppointmentNode* head;
    int currentId;

    AppointmentNode* createNode(int pid, int did,
                                const string& date,
                                const string& slot) {
        AppointmentNode* node = new AppointmentNode();
        node->appointmentId = currentId++;
        node->patientId = pid;
        node->doctorId = did;
        node->date = date;
        node->timeSlot = slot;
        node->status = "SCHEDULED";
        node->next = nullptr;
        return node;
    }

public:
    AppointmentManager() : head(nullptr), currentId(1) {}

    bool isSlotFree(int doctorId,
                    const string& date,
                    const string& timeSlot) const {
        AppointmentNode* temp = head;
        while (temp) {
            if (temp->doctorId == doctorId &&
                temp->date == date &&
                temp->timeSlot == timeSlot &&
                temp->status == "SCHEDULED") {
                return false;
            }
            temp = temp->next;
        }
        return true;
    }

    void add(int patientId,
             int doctorId,
             const string& date,
             const string& timeSlot) {
        AppointmentNode* node =
            createNode(patientId, doctorId, date, timeSlot);
        node->next = head;
        head = node;
    }

    void save(const string& file) const {
        ofstream out(file);
        AppointmentNode* temp = head;

        while (temp) {
            out << temp->appointmentId << ","
                << temp->patientId << ","
                << temp->doctorId << ","
                << temp->date << ","
                << temp->timeSlot << ","
                << temp->status << "\n";
            temp = temp->next;
        }
    }

    void load(const string& file) {
        ifstream in(file);
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

            currentId = max(currentId, node->appointmentId + 1);
        }
    }
};

AppointmentManager appointmentManager;

/* =====================================================
   DATA STRUCTURE: GRAPH (BFS)
   PURPOSE: Doctor Time Slot Availability
   ===================================================== */

struct TimeSlot {
    string slot;
    bool isAvailable;
    int doctorId;
};

class TimeSlotManager {
private:
    map<string, vector<TimeSlot>> dayGraph;

    vector<string> standardSlots() const {
        return {
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        };
    }

public:
    void prepareDay(const string& date, int doctorId) {
        for (const string& s : standardSlots()) {
            bool free =
                appointmentManager.isSlotFree(doctorId, date, s);
            dayGraph[date].push_back({ s, free, doctorId });
        }
    }

    vector<string> availableSlots(const string& date, int doctorId) {
        if (!dayGraph.count(date)) {
            prepareDay(date, doctorId);
        }

        vector<string> result;
        queue<TimeSlot> q;

        for (const auto& slot : dayGraph[date]) {
            if (slot.doctorId == doctorId) {
                q.push(slot);
            }
        }

        while (!q.empty()) {
            TimeSlot current = q.front();
            q.pop();

            if (current.isAvailable) {
                result.push_back(current.slot);
            }
        }
        return result;
    }

    void bookSlot(const string& date,
                  const string& slot,
                  int doctorId) {
        for (auto& s : dayGraph[date]) {
            if (s.slot == slot && s.doctorId == doctorId) {
                s.isAvailable = false;
                return;
            }
        }
    }
};

TimeSlotManager slotManager;

/* =====================================================
   FILE OPERATIONS (GUI SAFE)
   ===================================================== */

const string DATA_DIR = "../DataFiles/";

void processScheduleRequest() {
    ifstream in(DATA_DIR + "schedule_input.txt");
    string record;
    getline(in, record);

    stringstream ss(record);
    string temp, date, slot;
    int patientId, doctorId;

    getline(ss, temp, ','); patientId = stoi(temp);
    getline(ss, temp, ','); doctorId = stoi(temp);
    getline(ss, date, ',');
    getline(ss, slot, ',');

    ofstream out(DATA_DIR + "schedule_output.txt");

    if (appointmentManager.isSlotFree(doctorId, date, slot)) {
        appointmentManager.add(patientId, doctorId, date, slot);
        slotManager.bookSlot(date, slot, doctorId);

        out << "SUCCESS\n";
        out << "Appointment confirmed: "
            << date << " " << slot << "\n";
    }
    else {
        out << "ERROR: Slot already booked\n";
    }

    appointmentManager.save(DATA_DIR + "appointments.txt");
}

void processSlotQuery() {
    ifstream in(DATA_DIR + "slots_query.txt");
    int doctorId;
    string date;

    in >> doctorId;
    in.ignore();
    getline(in, date);

    vector<string> slots =
        slotManager.availableSlots(date, doctorId);

    ofstream out(DATA_DIR + "available_slots.txt");
    out << "AVAILABLE_SLOTS\n";
    for (const string& s : slots) {
        out << s << "\n";
    }
}

void exportAppointments() {
    appointmentManager.save(
        DATA_DIR + "all_appointments.txt"
    );
}

/* =====================================================
   MAIN ENTRY POINT
   ===================================================== */

int main() {
    appointmentManager.load(
        DATA_DIR + "appointments.txt"
    );

    ifstream cmd(DATA_DIR + "schedule_command.txt");
    string command;
    getline(cmd, command);

    if (command == "SCHEDULE_APPOINTMENT")
        processScheduleRequest();
    else if (command == "GET_AVAILABLE_SLOTS")
        processSlotQuery();
    else if (command == "GET_ALL_APPOINTMENTS")
        exportAppointments();

    return 0;
}
