if redis.call('setxx', KEYS[1], ARGV[1]) == 1 then
    if redis.call('get', KEYS[1]) == ARGV[1] then
        return redis.call('expire', KEYS[1], ARGV[2])
    else
        return 0
    end
end
