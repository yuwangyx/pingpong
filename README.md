# Creating a Ping-Pong Demo with Rate Limiting
Introduction
In this setup, we simulate a simple ping-pong scenario involving multiple ping services. These services utilize file locks to coordinate and ensure distributed locking mechanisms, thereby restricting the overall throughput to 2 Requests Per Second (RPS). Meanwhile, the pong service incorporates the token bucket algorithm to self-regulate its operation speed, maintaining a steady processing rate of 1 Request Per Second (RPS).
