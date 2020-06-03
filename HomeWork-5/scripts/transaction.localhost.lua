math.randomseed(os.time())
math.random(); math.random(); math.random()

request = function()
  path = "/public/transaction?id=" .. math.random(1038210)
  -- Return the request object with the current URL path
  return wrk.format('GET', path, {['Host'] = 'localhost'})
end

logfile = io.open("wrk-" .. os.date('%Y-%m-%d_%H_%M_%S') .. ".log", "a+");

response = function(status, header, body)
     logfile:write("status:" .. status .. "\n" .. body .. "\n-------------------------------------------------\n");
end
