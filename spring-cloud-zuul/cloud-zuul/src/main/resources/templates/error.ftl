<!DOCTYPE html>
<html>
<head>
    <meta name="robots" content="noindex,nofollow" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    <style>
        h2{
            color: #4288ce;
            font-weight: 400;
            padding: 6px 0;
            margin: 6px 0 0;
            font-size: 18px;
            border-bottom: 1px solid #eee;
        }

        /* Exception Variables */
        .exception-var table{
            width: 100%;
            max-width: 500px;
            margin: 12px 0;
            box-sizing: border-box;
            table-layout:fixed;
            word-wrap:break-word;
        }
        .exception-var table caption{
            text-align: left;
            font-size: 16px;
            font-weight: bold;
            padding: 6px 0;
        }
        .exception-var table caption small{
            font-weight: 300;
            display: inline-block;
            margin-left: 10px;
            color: #ccc;
        }
        .exception-var table tbody{
            font-size: 13px;
            font-family: Consolas,"Liberation Mono",Courier,"微软雅黑";
        }
        .exception-var table td{
            padding: 0 6px;
            vertical-align: top;
            word-break: break-all;
        }
        .exception-var table td:first-child{
            width: 28%;
            font-weight: bold;
            white-space: nowrap;
        }
        .exception-var table td pre{
            margin: 0;
        }
    </style>
</head>
<body>

<div class="exception-var">
    <h2>Exception Datas</h2>
    <table>
        <tbody>
        <tr>
            <td>Code</td>
            <td>
                ${(exception.code)!}
            </td>
        </tr>
        <tr>
            <td>Time</td>
            <td>
                ${(exception.timestamp?datetime)!}
            </td>
        </tr>
        <tr>
            <td>Path</td>
            <td>
                ${(exception.path)!}
            </td>
        </tr>
        <tr>
            <td>Exception</td>
            <td>
                ${(exception.error)!}
            </td>
        </tr>
        <tr>
            <td>Message</td>
            <td>
                ${(exception.message)!}
            </td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>