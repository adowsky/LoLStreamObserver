import React from 'react';

export default class TableHeader extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return <div className="row content-header">
            <div className="col">
                <span>Streamer Name</span>
            </div>
            <div className="col">
                <span>Summoner Name</span>
            </div>
        </div>
    }
}