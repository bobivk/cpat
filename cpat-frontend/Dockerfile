# Step 1: Use official Node.js image to build the Angular app
FROM node:22 AS build

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy package.json and package-lock.json
COPY package*.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy all project files
COPY . .

# Step 6: Build the Angular app
RUN npm run build

# Step 7: Use an Nginx server to serve the app
FROM nginx:alpine

# Step 8: Copy the build output from the previous image
COPY --from=build /app/dist/cpat-frontend /usr/share/nginx/html

# Step 9: Expose port 80
EXPOSE 80

# Step 10: Start Nginx server
CMD ["nginx", "-g", "daemon off;"]
