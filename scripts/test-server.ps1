# Set environment variables for test mode
$env:NODE_ENV = "test"
$env:PORT = "8081"

# Start the test server
npx ts-node server/src/server.ts
